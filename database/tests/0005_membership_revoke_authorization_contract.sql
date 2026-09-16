\set ON_ERROR_STOP on

-- Dynamic membership-revoke authorization contract.
--
-- The important boundary is a lifecycle transition, not merely a user who was already inactive:
-- an ACTIVE Player who owns/controls an existing hosted PC must lose hosted read/write access as
-- soon as the membership becomes KICKED or BANNED, while the hosted PC itself remains intact and
-- an independently authorized ACTIVE DM continues to have campaign authority. Revoking the DM
-- must likewise remove hosted PC authority. This mirrors the predicates used by the Worker store.

INSERT INTO app_user(id, descope_subject, display_name)
VALUES
    ('10000000-0000-4000-8000-000000000401', 'revoke-owner', 'Revoked Owner'),
    ('10000000-0000-4000-8000-000000000402', 'revoke-dm', 'Revoked DM');

INSERT INTO campaign(id, name, revision)
VALUES ('20000000-0000-4000-8000-000000000401', 'Revoke Authorization Campaign', 0);

INSERT INTO campaign_membership(campaign_id, user_id, role, status)
VALUES
    ('20000000-0000-4000-8000-000000000401', '10000000-0000-4000-8000-000000000401', 'PLAYER', 'ACTIVE'),
    ('20000000-0000-4000-8000-000000000401', '10000000-0000-4000-8000-000000000402', 'DM', 'ACTIVE');

INSERT INTO pc(
    id,
    campaign_id,
    owner_user_id,
    controller_user_id,
    name,
    revision,
    snapshot_format,
    snapshot_version,
    snapshot,
    reconciled_at
)
VALUES (
    '40000000-0000-4000-8000-000000000401',
    '20000000-0000-4000-8000-000000000401',
    '10000000-0000-4000-8000-000000000401',
    '10000000-0000-4000-8000-000000000401',
    'Owner PC',
    0,
    'dnd-custom-aid.character-backup',
    2,
    '{"format":"dnd-custom-aid.character-backup","version":2,"character":{"id":"40000000-0000-4000-8000-000000000401","campaignId":"20000000-0000-4000-8000-000000000401","name":"Owner PC"}}'::jsonb,
    now()
);

DO $$
DECLARE
    row_count integer;
    current_pc record;
    lifecycle_status text;
BEGIN
    -- The ACTIVE owner can initially read the existing PC.
    SELECT count(*) INTO row_count
    FROM pc p
    JOIN campaign c ON c.id = p.campaign_id
    JOIN campaign_membership m ON m.campaign_id = p.campaign_id
    WHERE p.campaign_id = '20000000-0000-4000-8000-000000000401'::uuid
      AND p.id = '40000000-0000-4000-8000-000000000401'::uuid
      AND m.user_id = '10000000-0000-4000-8000-000000000401'::uuid
      AND m.status = 'ACTIVE'
      AND c.deleted_at IS NULL
      AND (m.role = 'DM' OR p.owner_user_id = m.user_id OR p.controller_user_id = m.user_id);
    IF row_count <> 1 THEN
        RAISE EXCEPTION 'active owner could not read the owned PC before revoke';
    END IF;

    -- The ACTIVE owner can initially update the exact hosted revision.
    WITH membership AS (
        SELECT m.role
        FROM campaign_membership m
        JOIN campaign c ON c.id = m.campaign_id
        WHERE m.campaign_id = '20000000-0000-4000-8000-000000000401'::uuid
          AND m.user_id = '10000000-0000-4000-8000-000000000401'::uuid
          AND m.status = 'ACTIVE'
          AND c.deleted_at IS NULL
    ),
    existing_pc AS (
        SELECT p.*
        FROM pc p
        WHERE p.id = '40000000-0000-4000-8000-000000000401'::uuid
    ),
    permitted AS (
        SELECT 1
        FROM membership m
        WHERE m.role = 'DM'
           OR EXISTS (
                SELECT 1
                FROM existing_pc p
                WHERE p.owner_user_id = '10000000-0000-4000-8000-000000000401'::uuid
                   OR p.controller_user_id = '10000000-0000-4000-8000-000000000401'::uuid
           )
    ),
    updated_pc AS (
        UPDATE pc p
        SET name = 'Owner PC Before Revoke',
            revision = p.revision + 1,
            updated_at = now()
        WHERE p.id = '40000000-0000-4000-8000-000000000401'::uuid
          AND p.campaign_id = '20000000-0000-4000-8000-000000000401'::uuid
          AND p.deleted_at IS NULL
          AND p.revision = 0
          AND EXISTS (SELECT 1 FROM existing_pc)
          AND EXISTS (SELECT 1 FROM permitted)
        RETURNING p.id
    )
    SELECT count(*) INTO row_count FROM updated_pc;
    IF row_count <> 1 THEN
        RAISE EXCEPTION 'active owner could not update the owned PC before revoke';
    END IF;

    -- Explicit revoke: the lifecycle row remains, but hosted campaign/PC access ends.
    UPDATE campaign_membership
    SET status = 'KICKED', updated_at = now()
    WHERE campaign_id = '20000000-0000-4000-8000-000000000401'::uuid
      AND user_id = '10000000-0000-4000-8000-000000000401'::uuid;

    SELECT status::text INTO lifecycle_status
    FROM campaign_membership
    WHERE campaign_id = '20000000-0000-4000-8000-000000000401'::uuid
      AND user_id = '10000000-0000-4000-8000-000000000401'::uuid;
    IF lifecycle_status <> 'KICKED' THEN
        RAISE EXCEPTION 'explicit kicked lifecycle state was not retained';
    END IF;

    SELECT count(*) INTO row_count
    FROM campaign_membership m
    JOIN campaign c ON c.id = m.campaign_id
    WHERE m.user_id = '10000000-0000-4000-8000-000000000401'::uuid
      AND m.status = 'ACTIVE'
      AND c.deleted_at IS NULL;
    IF row_count <> 0 THEN
        RAISE EXCEPTION 'kicked Player remained in the active campaign projection';
    END IF;

    SELECT count(*) INTO row_count
    FROM pc p
    JOIN campaign c ON c.id = p.campaign_id
    JOIN campaign_membership m ON m.campaign_id = p.campaign_id
    WHERE p.campaign_id = '20000000-0000-4000-8000-000000000401'::uuid
      AND p.id = '40000000-0000-4000-8000-000000000401'::uuid
      AND m.user_id = '10000000-0000-4000-8000-000000000401'::uuid
      AND m.status = 'ACTIVE'
      AND c.deleted_at IS NULL
      AND (m.role = 'DM' OR p.owner_user_id = m.user_id OR p.controller_user_id = m.user_id);
    IF row_count <> 0 THEN
        RAISE EXCEPTION 'kicked owner retained hosted PC read access';
    END IF;

    -- Even with the exact current revision and unchanged owner/controller identity, revoke blocks write.
    WITH membership AS (
        SELECT m.role
        FROM campaign_membership m
        JOIN campaign c ON c.id = m.campaign_id
        WHERE m.campaign_id = '20000000-0000-4000-8000-000000000401'::uuid
          AND m.user_id = '10000000-0000-4000-8000-000000000401'::uuid
          AND m.status = 'ACTIVE'
          AND c.deleted_at IS NULL
    ),
    existing_pc AS (
        SELECT p.*
        FROM pc p
        WHERE p.id = '40000000-0000-4000-8000-000000000401'::uuid
    ),
    permitted AS (
        SELECT 1
        FROM membership m
        WHERE m.role = 'DM'
           OR EXISTS (
                SELECT 1
                FROM existing_pc p
                WHERE p.owner_user_id = '10000000-0000-4000-8000-000000000401'::uuid
                   OR p.controller_user_id = '10000000-0000-4000-8000-000000000401'::uuid
           )
    ),
    updated_pc AS (
        UPDATE pc p
        SET name = 'Revoked Owner Must Not Write',
            revision = p.revision + 1,
            updated_at = now()
        WHERE p.id = '40000000-0000-4000-8000-000000000401'::uuid
          AND p.campaign_id = '20000000-0000-4000-8000-000000000401'::uuid
          AND p.deleted_at IS NULL
          AND p.revision = 1
          AND EXISTS (SELECT 1 FROM existing_pc)
          AND EXISTS (SELECT 1 FROM permitted)
        RETURNING p.id
    )
    SELECT count(*) INTO row_count FROM updated_pc;
    IF row_count <> 0 THEN
        RAISE EXCEPTION 'kicked owner unexpectedly retained hosted PC write authority';
    END IF;

    SELECT revision, name, owner_user_id, controller_user_id
    INTO current_pc
    FROM pc
    WHERE id = '40000000-0000-4000-8000-000000000401'::uuid;
    IF NOT FOUND OR current_pc.revision <> 1 OR current_pc.name <> 'Owner PC Before Revoke' THEN
        RAISE EXCEPTION 'revoking membership mutated or deleted hosted PC state: %', current_pc;
    END IF;
    IF current_pc.owner_user_id <> '10000000-0000-4000-8000-000000000401'::uuid
       OR current_pc.controller_user_id <> '10000000-0000-4000-8000-000000000401'::uuid THEN
        RAISE EXCEPTION 'revoking membership silently rewrote PC ownership/control: %', current_pc;
    END IF;

    -- DM authority is independent of Player ownership and remains valid while the DM is ACTIVE.
    SELECT count(*) INTO row_count
    FROM pc p
    JOIN campaign c ON c.id = p.campaign_id
    JOIN campaign_membership m ON m.campaign_id = p.campaign_id
    WHERE p.id = '40000000-0000-4000-8000-000000000401'::uuid
      AND m.user_id = '10000000-0000-4000-8000-000000000402'::uuid
      AND m.status = 'ACTIVE'
      AND c.deleted_at IS NULL
      AND (m.role = 'DM' OR p.owner_user_id = m.user_id OR p.controller_user_id = m.user_id);
    IF row_count <> 1 THEN
        RAISE EXCEPTION 'active DM lost independent authority after Player revoke';
    END IF;

    WITH membership AS (
        SELECT m.role
        FROM campaign_membership m
        JOIN campaign c ON c.id = m.campaign_id
        WHERE m.campaign_id = '20000000-0000-4000-8000-000000000401'::uuid
          AND m.user_id = '10000000-0000-4000-8000-000000000402'::uuid
          AND m.status = 'ACTIVE'
          AND c.deleted_at IS NULL
    ),
    existing_pc AS (
        SELECT p.*
        FROM pc p
        WHERE p.id = '40000000-0000-4000-8000-000000000401'::uuid
    ),
    permitted AS (
        SELECT 1
        FROM membership m
        WHERE m.role = 'DM'
           OR EXISTS (
                SELECT 1
                FROM existing_pc p
                WHERE p.owner_user_id = '10000000-0000-4000-8000-000000000402'::uuid
                   OR p.controller_user_id = '10000000-0000-4000-8000-000000000402'::uuid
           )
    ),
    updated_pc AS (
        UPDATE pc p
        SET name = 'DM Correction After Owner Revoke',
            revision = p.revision + 1,
            updated_at = now()
        WHERE p.id = '40000000-0000-4000-8000-000000000401'::uuid
          AND p.campaign_id = '20000000-0000-4000-8000-000000000401'::uuid
          AND p.deleted_at IS NULL
          AND p.revision = 1
          AND EXISTS (SELECT 1 FROM existing_pc)
          AND EXISTS (SELECT 1 FROM permitted)
        RETURNING p.id
    )
    SELECT count(*) INTO row_count FROM updated_pc;
    IF row_count <> 1 THEN
        RAISE EXCEPTION 'active DM could not correct PC after Player revoke';
    END IF;

    -- BANNED is also explicitly inactive and must not restore access.
    UPDATE campaign_membership
    SET status = 'BANNED', updated_at = now()
    WHERE campaign_id = '20000000-0000-4000-8000-000000000401'::uuid
      AND user_id = '10000000-0000-4000-8000-000000000401'::uuid;

    SELECT status::text INTO lifecycle_status
    FROM campaign_membership
    WHERE campaign_id = '20000000-0000-4000-8000-000000000401'::uuid
      AND user_id = '10000000-0000-4000-8000-000000000401'::uuid;
    IF lifecycle_status <> 'BANNED' THEN
        RAISE EXCEPTION 'explicit banned lifecycle state was not retained';
    END IF;

    SELECT count(*) INTO row_count
    FROM pc p
    JOIN campaign c ON c.id = p.campaign_id
    JOIN campaign_membership m ON m.campaign_id = p.campaign_id
    WHERE p.id = '40000000-0000-4000-8000-000000000401'::uuid
      AND m.user_id = '10000000-0000-4000-8000-000000000401'::uuid
      AND m.status = 'ACTIVE'
      AND c.deleted_at IS NULL
      AND (m.role = 'DM' OR p.owner_user_id = m.user_id OR p.controller_user_id = m.user_id);
    IF row_count <> 0 THEN
        RAISE EXCEPTION 'banned owner unexpectedly regained hosted PC access';
    END IF;

    -- Revoking DM membership also removes campaign authority without deleting the PC.
    UPDATE campaign_membership
    SET status = 'KICKED', updated_at = now()
    WHERE campaign_id = '20000000-0000-4000-8000-000000000401'::uuid
      AND user_id = '10000000-0000-4000-8000-000000000402'::uuid;

    SELECT count(*) INTO row_count
    FROM pc p
    JOIN campaign c ON c.id = p.campaign_id
    JOIN campaign_membership m ON m.campaign_id = p.campaign_id
    WHERE p.id = '40000000-0000-4000-8000-000000000401'::uuid
      AND m.user_id = '10000000-0000-4000-8000-000000000402'::uuid
      AND m.status = 'ACTIVE'
      AND c.deleted_at IS NULL
      AND (m.role = 'DM' OR p.owner_user_id = m.user_id OR p.controller_user_id = m.user_id);
    IF row_count <> 0 THEN
        RAISE EXCEPTION 'kicked DM unexpectedly retained hosted PC read authority';
    END IF;

    WITH membership AS (
        SELECT m.role
        FROM campaign_membership m
        JOIN campaign c ON c.id = m.campaign_id
        WHERE m.campaign_id = '20000000-0000-4000-8000-000000000401'::uuid
          AND m.user_id = '10000000-0000-4000-8000-000000000402'::uuid
          AND m.status = 'ACTIVE'
          AND c.deleted_at IS NULL
    ),
    existing_pc AS (
        SELECT p.*
        FROM pc p
        WHERE p.id = '40000000-0000-4000-8000-000000000401'::uuid
    ),
    permitted AS (
        SELECT 1
        FROM membership m
        WHERE m.role = 'DM'
           OR EXISTS (
                SELECT 1
                FROM existing_pc p
                WHERE p.owner_user_id = '10000000-0000-4000-8000-000000000402'::uuid
                   OR p.controller_user_id = '10000000-0000-4000-8000-000000000402'::uuid
           )
    ),
    updated_pc AS (
        UPDATE pc p
        SET name = 'Revoked DM Must Not Write',
            revision = p.revision + 1,
            updated_at = now()
        WHERE p.id = '40000000-0000-4000-8000-000000000401'::uuid
          AND p.campaign_id = '20000000-0000-4000-8000-000000000401'::uuid
          AND p.deleted_at IS NULL
          AND p.revision = 2
          AND EXISTS (SELECT 1 FROM existing_pc)
          AND EXISTS (SELECT 1 FROM permitted)
        RETURNING p.id
    )
    SELECT count(*) INTO row_count FROM updated_pc;
    IF row_count <> 0 THEN
        RAISE EXCEPTION 'kicked DM unexpectedly retained hosted PC write authority';
    END IF;

    SELECT revision, name, owner_user_id, controller_user_id
    INTO current_pc
    FROM pc
    WHERE id = '40000000-0000-4000-8000-000000000401'::uuid;
    IF NOT FOUND OR current_pc.revision <> 2 OR current_pc.name <> 'DM Correction After Owner Revoke' THEN
        RAISE EXCEPTION 'membership revoke unexpectedly mutated/deleted hosted PC after DM revoke: %', current_pc;
    END IF;
END
$$;
