BEGIN;

-- Base para DM, package 1: hosted DM content (creatures, NPCs, homebrew rules, places, zones,
-- encounters, scenes and later DM-authored families).
--
-- One row per reusable content object. The scope never changes after creation:
--   * PERSONAL: the DM's own library, readable and writable only by owner_user_id;
--   * CAMPAIGN: campaign material, readable and writable only by an ACTIVE DM of the campaign.
-- Players never read DM content through this table (player-facing handouts are a later package).
--
-- The family is stored as text without a closed list so later DM families (packages, dungeon
-- activities) travel through the same contract. The snapshot is the complete client document.
CREATE TABLE dm_content (
    id uuid PRIMARY KEY,
    scope_kind text NOT NULL CHECK (scope_kind IN ('PERSONAL', 'CAMPAIGN')),
    owner_user_id uuid REFERENCES app_user(id) ON DELETE CASCADE,
    campaign_id uuid REFERENCES campaign(id) ON DELETE CASCADE,
    family text NOT NULL CHECK (family ~ '^[A-Z][A-Z0-9_]{0,63}$'),
    display_name text NOT NULL CHECK (length(btrim(display_name)) > 0),
    revision bigint NOT NULL DEFAULT 0 CHECK (revision >= 0),
    deleted_at timestamptz,
    snapshot_format text NOT NULL CHECK (length(btrim(snapshot_format)) > 0),
    snapshot_version integer NOT NULL CHECK (snapshot_version >= 1),
    snapshot jsonb NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),
    CHECK (
        (scope_kind = 'PERSONAL' AND owner_user_id IS NOT NULL AND campaign_id IS NULL)
        OR
        (scope_kind = 'CAMPAIGN' AND campaign_id IS NOT NULL AND owner_user_id IS NULL)
    )
);

CREATE INDEX dm_content_campaign_idx
    ON dm_content(campaign_id)
    WHERE campaign_id IS NOT NULL;

CREATE INDEX dm_content_owner_idx
    ON dm_content(owner_user_id)
    WHERE owner_user_id IS NOT NULL;

-- Errors use a private SQLSTATE class (DC) so the Worker maps them to API errors exactly:
--   DC403 forbidden, DC404 not found, DC409 stale revision (DETAIL = current revision),
--   DC410 deleted, DC412 mutation identity reused, DC422 scope or identity mismatch.

CREATE OR REPLACE FUNCTION dm_content_actor_may_write(
    p_actor uuid,
    p_scope_kind text,
    p_owner_user_id uuid,
    p_campaign_id uuid
)
RETURNS boolean
LANGUAGE sql
STABLE
AS $$
    SELECT CASE
        WHEN p_scope_kind = 'PERSONAL' THEN p_owner_user_id = p_actor
        WHEN p_scope_kind = 'CAMPAIGN' THEN EXISTS (
            SELECT 1
            FROM campaign_membership m
            JOIN campaign c ON c.id = m.campaign_id
            WHERE m.campaign_id = p_campaign_id
              AND m.user_id = p_actor
              AND m.role = 'DM'
              AND m.status = 'ACTIVE'
              AND c.deleted_at IS NULL
        )
        ELSE false
    END
$$;

CREATE OR REPLACE FUNCTION dm_content_put(
    p_actor uuid,
    p_mutation uuid,
    p_content uuid,
    p_expected_revision bigint,
    p_scope_kind text,
    p_scope_ref uuid,
    p_family text,
    p_display_name text,
    p_snapshot_format text,
    p_snapshot_version integer,
    p_snapshot jsonb
)
RETURNS TABLE(content_id uuid, resulting_revision bigint, applied boolean)
LANGUAGE plpgsql
AS $$
#variable_conflict use_column
DECLARE
    v_owner uuid := CASE WHEN p_scope_kind = 'PERSONAL' THEN p_scope_ref ELSE NULL END;
    v_campaign uuid := CASE WHEN p_scope_kind = 'CAMPAIGN' THEN p_scope_ref ELSE NULL END;
    v_receipt mutation_receipt%ROWTYPE;
    v_existing dm_content%ROWTYPE;
BEGIN
    IF p_expected_revision IS NULL OR p_expected_revision < 0 THEN
        RAISE EXCEPTION 'Expected revision must be a non-negative integer.' USING ERRCODE = 'DC422';
    END IF;
    IF p_scope_kind NOT IN ('PERSONAL', 'CAMPAIGN') OR p_scope_ref IS NULL THEN
        RAISE EXCEPTION 'Content scope is invalid.' USING ERRCODE = 'DC422';
    END IF;

    PERFORM pg_advisory_xact_lock(hashtextextended(p_actor::text || ':' || p_mutation::text, 0));
    PERFORM pg_advisory_xact_lock(hashtextextended('DM_CONTENT:' || p_content::text, 0));

    SELECT * INTO v_receipt
    FROM mutation_receipt r
    WHERE r.user_id = p_actor AND r.mutation_id = p_mutation;

    SELECT * INTO v_existing FROM dm_content d WHERE d.id = p_content;

    IF FOUND THEN
        IF NOT dm_content_actor_may_write(p_actor, v_existing.scope_kind, v_existing.owner_user_id, v_existing.campaign_id) THEN
            RAISE EXCEPTION 'The account may not change this DM content.' USING ERRCODE = 'DC403';
        END IF;
    ELSIF NOT dm_content_actor_may_write(p_actor, p_scope_kind, v_owner, v_campaign) THEN
        RAISE EXCEPTION 'The account may not create DM content in this scope.' USING ERRCODE = 'DC403';
    END IF;

    IF v_receipt.mutation_id IS NOT NULL THEN
        IF v_receipt.object_type = 'DM_CONTENT' AND v_receipt.object_id = p_content THEN
            RETURN QUERY SELECT p_content, v_existing.revision, false;
            RETURN;
        END IF;
        RAISE EXCEPTION 'Mutation identity is already used.' USING ERRCODE = 'DC412';
    END IF;

    IF v_existing.id IS NULL THEN
        IF p_expected_revision <> 0 THEN
            RAISE EXCEPTION 'DM content does not exist yet.' USING ERRCODE = 'DC404';
        END IF;
        INSERT INTO dm_content(
            id, scope_kind, owner_user_id, campaign_id, family, display_name, revision,
            snapshot_format, snapshot_version, snapshot
        )
        VALUES (
            p_content, p_scope_kind, v_owner, v_campaign, p_family, btrim(p_display_name), 0,
            p_snapshot_format, p_snapshot_version, p_snapshot
        );
        INSERT INTO mutation_receipt(user_id, mutation_id, object_type, object_id, resulting_revision)
        VALUES (p_actor, p_mutation, 'DM_CONTENT', p_content, 0);
        RETURN QUERY SELECT p_content, 0::bigint, true;
        RETURN;
    END IF;

    IF v_existing.scope_kind <> p_scope_kind
        OR v_existing.owner_user_id IS DISTINCT FROM v_owner
        OR v_existing.campaign_id IS DISTINCT FROM v_campaign THEN
        RAISE EXCEPTION 'DM content scope cannot change.' USING ERRCODE = 'DC422';
    END IF;
    IF v_existing.family <> p_family THEN
        RAISE EXCEPTION 'DM content family cannot change.' USING ERRCODE = 'DC422';
    END IF;
    IF v_existing.deleted_at IS NOT NULL THEN
        RAISE EXCEPTION 'DM content has been deleted.' USING ERRCODE = 'DC410';
    END IF;
    IF v_existing.revision <> p_expected_revision THEN
        RAISE EXCEPTION 'DM content revision no longer matches.'
            USING ERRCODE = 'DC409', DETAIL = v_existing.revision::text;
    END IF;

    UPDATE dm_content d
    SET display_name = btrim(p_display_name),
        revision = d.revision + 1,
        snapshot_format = p_snapshot_format,
        snapshot_version = p_snapshot_version,
        snapshot = p_snapshot,
        updated_at = now()
    WHERE d.id = p_content;

    INSERT INTO mutation_receipt(user_id, mutation_id, object_type, object_id, resulting_revision)
    VALUES (p_actor, p_mutation, 'DM_CONTENT', p_content, v_existing.revision + 1);
    RETURN QUERY SELECT p_content, v_existing.revision + 1, true;
END;
$$;

CREATE OR REPLACE FUNCTION dm_content_delete(
    p_actor uuid,
    p_mutation uuid,
    p_content uuid,
    p_expected_revision bigint
)
RETURNS TABLE(content_id uuid, resulting_revision bigint, applied boolean)
LANGUAGE plpgsql
AS $$
#variable_conflict use_column
DECLARE
    v_receipt mutation_receipt%ROWTYPE;
    v_existing dm_content%ROWTYPE;
BEGIN
    IF p_expected_revision IS NULL OR p_expected_revision < 0 THEN
        RAISE EXCEPTION 'Expected revision must be a non-negative integer.' USING ERRCODE = 'DC422';
    END IF;

    PERFORM pg_advisory_xact_lock(hashtextextended(p_actor::text || ':' || p_mutation::text, 0));
    PERFORM pg_advisory_xact_lock(hashtextextended('DM_CONTENT:' || p_content::text, 0));

    SELECT * INTO v_receipt
    FROM mutation_receipt r
    WHERE r.user_id = p_actor AND r.mutation_id = p_mutation;

    SELECT * INTO v_existing FROM dm_content d WHERE d.id = p_content;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'DM content was not found.' USING ERRCODE = 'DC404';
    END IF;
    IF NOT dm_content_actor_may_write(p_actor, v_existing.scope_kind, v_existing.owner_user_id, v_existing.campaign_id) THEN
        RAISE EXCEPTION 'The account may not delete this DM content.' USING ERRCODE = 'DC403';
    END IF;

    IF v_receipt.mutation_id IS NOT NULL THEN
        IF v_receipt.object_type = 'DM_CONTENT_DELETE' AND v_receipt.object_id = p_content THEN
            RETURN QUERY SELECT p_content, v_existing.revision, false;
            RETURN;
        END IF;
        RAISE EXCEPTION 'Mutation identity is already used.' USING ERRCODE = 'DC412';
    END IF;

    IF v_existing.deleted_at IS NOT NULL THEN
        RAISE EXCEPTION 'DM content has been deleted.' USING ERRCODE = 'DC410';
    END IF;
    IF v_existing.revision <> p_expected_revision THEN
        RAISE EXCEPTION 'DM content revision no longer matches.'
            USING ERRCODE = 'DC409', DETAIL = v_existing.revision::text;
    END IF;

    -- Tombstone: the row stays (non-resurrection and recovery); the revision advances.
    UPDATE dm_content d
    SET revision = d.revision + 1,
        deleted_at = now(),
        updated_at = now()
    WHERE d.id = p_content;

    INSERT INTO mutation_receipt(user_id, mutation_id, object_type, object_id, resulting_revision)
    VALUES (p_actor, p_mutation, 'DM_CONTENT_DELETE', p_content, v_existing.revision + 1);
    RETURN QUERY SELECT p_content, v_existing.revision + 1, true;
END;
$$;

-- Reads include tombstones so every device learns about deletions (non-resurrection).
CREATE OR REPLACE FUNCTION dm_content_list_campaign(p_actor uuid, p_campaign uuid)
RETURNS SETOF dm_content
LANGUAGE plpgsql
STABLE
AS $$
BEGIN
    IF NOT dm_content_actor_may_write(p_actor, 'CAMPAIGN', NULL, p_campaign) THEN
        RAISE EXCEPTION 'Only an active DM of the campaign may read its DM content.' USING ERRCODE = 'DC403';
    END IF;
    RETURN QUERY
    SELECT d.*
    FROM dm_content d
    WHERE d.campaign_id = p_campaign
    ORDER BY d.id;
END;
$$;

CREATE OR REPLACE FUNCTION dm_content_list_personal(p_actor uuid)
RETURNS SETOF dm_content
LANGUAGE sql
STABLE
AS $$
    SELECT d.*
    FROM dm_content d
    WHERE d.owner_user_id = p_actor
    ORDER BY d.id
$$;

COMMIT;
