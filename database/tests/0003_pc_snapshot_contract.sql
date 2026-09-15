\set ON_ERROR_STOP on

-- Hosted PC current-state contract. This mirrors the Worker store semantics against disposable
-- PostgreSQL so authorization, optimistic revisions, mutation receipts and JSONB persistence are
-- proven without requiring Neon.

INSERT INTO app_user(id, descope_subject, display_name)
VALUES
    ('10000000-0000-4000-8000-000000000201', 'pc-owner', 'PC Owner'),
    ('10000000-0000-4000-8000-000000000202', 'pc-dm', 'PC DM'),
    ('10000000-0000-4000-8000-000000000203', 'pc-other-player', 'Other Player'),
    ('10000000-0000-4000-8000-000000000204', 'pc-kicked-player', 'Kicked Player');

INSERT INTO campaign(id, name, revision)
VALUES ('20000000-0000-4000-8000-000000000201', 'PC Contract Campaign', 0);

INSERT INTO campaign_membership(campaign_id, user_id, role, status)
VALUES
    ('20000000-0000-4000-8000-000000000201', '10000000-0000-4000-8000-000000000201', 'PLAYER', 'ACTIVE'),
    ('20000000-0000-4000-8000-000000000201', '10000000-0000-4000-8000-000000000202', 'DM', 'ACTIVE'),
    ('20000000-0000-4000-8000-000000000201', '10000000-0000-4000-8000-000000000203', 'PLAYER', 'ACTIVE'),
    ('20000000-0000-4000-8000-000000000201', '10000000-0000-4000-8000-000000000204', 'PLAYER', 'KICKED');

CREATE OR REPLACE FUNCTION test_put_pc_snapshot(
    p_actor uuid,
    p_mutation uuid,
    p_pc uuid,
    p_campaign uuid,
    p_expected_revision bigint,
    p_name text,
    p_snapshot_format text,
    p_snapshot_version integer,
    p_snapshot jsonb
)
RETURNS TABLE(
    id uuid,
    campaign_id uuid,
    owner_user_id uuid,
    controller_user_id uuid,
    name text,
    revision bigint,
    snapshot_format text,
    snapshot_version integer,
    snapshot jsonb,
    applied boolean
)
LANGUAGE plpgsql
AS $$
DECLARE
    mutation_lock_key text := p_actor::text || ':' || p_mutation::text;
    object_lock_key text := 'PC:' || p_pc::text;
BEGIN
    PERFORM pg_advisory_xact_lock(hashtextextended(mutation_lock_key, 0));
    PERFORM pg_advisory_xact_lock(hashtextextended(object_lock_key, 0));

    RETURN QUERY
    WITH existing_receipt AS (
        SELECT r.object_type, r.object_id, r.resulting_revision
        FROM mutation_receipt r
        WHERE r.user_id = p_actor
          AND r.mutation_id = p_mutation
    ),
    membership AS (
        SELECT m.role
        FROM campaign_membership m
        JOIN campaign c ON c.id = m.campaign_id
        WHERE m.campaign_id = p_campaign
          AND m.user_id = p_actor
          AND m.status = 'ACTIVE'
          AND c.deleted_at IS NULL
    ),
    existing_pc AS (
        SELECT p.*
        FROM pc p
        WHERE p.id = p_pc
    ),
    permitted AS (
        SELECT 1
        FROM membership m
        WHERE NOT EXISTS (SELECT 1 FROM existing_pc)
           OR m.role = 'DM'
           OR EXISTS (
                SELECT 1
                FROM existing_pc p
                WHERE p.owner_user_id = p_actor
                   OR p.controller_user_id = p_actor
           )
    ),
    replayed AS (
        SELECT p.*, false AS applied
        FROM existing_receipt r
        JOIN pc p ON p.id = r.object_id
        WHERE r.object_type = 'PC'
          AND r.object_id = p_pc
          AND p.campaign_id = p_campaign
          AND EXISTS (SELECT 1 FROM permitted)
    ),
    created_pc AS (
        INSERT INTO pc(
            id, campaign_id, owner_user_id, controller_user_id, name, revision,
            snapshot_format, snapshot_version, snapshot, reconciled_at
        )
        SELECT
            p_pc, p_campaign, p_actor, p_actor, btrim(p_name), 0,
            p_snapshot_format, p_snapshot_version, p_snapshot, now()
        WHERE NOT EXISTS (SELECT 1 FROM existing_receipt)
          AND NOT EXISTS (SELECT 1 FROM existing_pc)
          AND p_expected_revision = 0
          AND EXISTS (SELECT 1 FROM permitted)
        ON CONFLICT ON CONSTRAINT pc_pkey DO NOTHING
        RETURNING pc.*, true AS applied
    ),
    updated_pc AS (
        UPDATE pc p
        SET name = btrim(p_name),
            revision = p.revision + 1,
            snapshot_format = p_snapshot_format,
            snapshot_version = p_snapshot_version,
            snapshot = p_snapshot,
            reconciled_at = now(),
            updated_at = now()
        WHERE p.id = p_pc
          AND p.campaign_id = p_campaign
          AND p.deleted_at IS NULL
          AND p.revision = p_expected_revision
          AND NOT EXISTS (SELECT 1 FROM existing_receipt)
          AND EXISTS (SELECT 1 FROM existing_pc)
          AND EXISTS (SELECT 1 FROM permitted)
        RETURNING p.*, true AS applied
    ),
    applied_pc AS (
        SELECT * FROM created_pc
        UNION ALL
        SELECT * FROM updated_pc
    ),
    new_receipt AS (
        INSERT INTO mutation_receipt(user_id, mutation_id, object_type, object_id, resulting_revision)
        SELECT p_actor, p_mutation, 'PC', a.id, a.revision
        FROM applied_pc a
        RETURNING object_type, object_id, resulting_revision
    )
    SELECT
        r.id,
        r.campaign_id,
        r.owner_user_id,
        r.controller_user_id,
        r.name,
        r.revision,
        r.snapshot_format,
        r.snapshot_version,
        r.snapshot,
        r.applied
    FROM (
        SELECT * FROM replayed
        UNION ALL
        SELECT * FROM applied_pc
    ) r;
END
$$;

DO $$
DECLARE
    r record;
    row_count integer;
BEGIN
    -- First Player upload creates hosted current state and binds owner/controller to the uploader.
    SELECT * INTO r
    FROM test_put_pc_snapshot(
        '10000000-0000-4000-8000-000000000201',
        '30000000-0000-4000-8000-000000000201',
        '40000000-0000-4000-8000-000000000201',
        '20000000-0000-4000-8000-000000000201',
        0,
        '  Simkin  ',
        'dnd-custom-aid.character-backup',
        2,
        '{"format":"dnd-custom-aid.character-backup","version":2,"character":{"id":"40000000-0000-4000-8000-000000000201"}}'::jsonb
    );
    IF NOT FOUND OR r.applied IS DISTINCT FROM true THEN
        RAISE EXCEPTION 'first PC snapshot mutation was not applied';
    END IF;
    IF r.revision <> 0 OR r.name <> 'Simkin' THEN
        RAISE EXCEPTION 'first PC snapshot returned unexpected state: %', r;
    END IF;
    IF r.owner_user_id <> '10000000-0000-4000-8000-000000000201'::uuid
       OR r.controller_user_id <> '10000000-0000-4000-8000-000000000201'::uuid THEN
        RAISE EXCEPTION 'first uploader did not become initial owner/controller';
    END IF;
    IF r.snapshot_format <> 'dnd-custom-aid.character-backup' OR r.snapshot_version <> 2 THEN
        RAISE EXCEPTION 'snapshot envelope metadata was not persisted';
    END IF;

    -- Same mutation identity is an idempotent replay, not another revision.
    SELECT * INTO r
    FROM test_put_pc_snapshot(
        '10000000-0000-4000-8000-000000000201',
        '30000000-0000-4000-8000-000000000201',
        '40000000-0000-4000-8000-000000000201',
        '20000000-0000-4000-8000-000000000201',
        0,
        'Simkin',
        'dnd-custom-aid.character-backup',
        2,
        '{"format":"dnd-custom-aid.character-backup","version":2,"character":{"id":"40000000-0000-4000-8000-000000000201"}}'::jsonb
    );
    IF NOT FOUND OR r.applied IS DISTINCT FROM false OR r.revision <> 0 THEN
        RAISE EXCEPTION 'PC idempotent replay did not return revision zero without reapplying';
    END IF;

    -- Owner update at exact revision advances once.
    SELECT * INTO r
    FROM test_put_pc_snapshot(
        '10000000-0000-4000-8000-000000000201',
        '30000000-0000-4000-8000-000000000202',
        '40000000-0000-4000-8000-000000000201',
        '20000000-0000-4000-8000-000000000201',
        0,
        'Simkin Updated',
        'dnd-custom-aid.character-backup',
        2,
        '{"format":"dnd-custom-aid.character-backup","version":2,"character":{"id":"40000000-0000-4000-8000-000000000201"},"marker":"owner-update"}'::jsonb
    );
    IF NOT FOUND OR r.applied IS DISTINCT FROM true OR r.revision <> 1 OR r.name <> 'Simkin Updated' THEN
        RAISE EXCEPTION 'owner update did not advance exactly once: %', r;
    END IF;

    -- Stale expected revision applies nothing and writes no receipt.
    SELECT count(*) INTO row_count
    FROM test_put_pc_snapshot(
        '10000000-0000-4000-8000-000000000201',
        '30000000-0000-4000-8000-000000000203',
        '40000000-0000-4000-8000-000000000201',
        '20000000-0000-4000-8000-000000000201',
        0,
        'Stale Attempt',
        'dnd-custom-aid.character-backup',
        2,
        '{"format":"dnd-custom-aid.character-backup","version":2,"marker":"stale"}'::jsonb
    );
    IF row_count <> 0 THEN
        RAISE EXCEPTION 'stale revision unexpectedly mutated PC state';
    END IF;
    IF EXISTS (
        SELECT 1 FROM mutation_receipt
        WHERE user_id = '10000000-0000-4000-8000-000000000201'
          AND mutation_id = '30000000-0000-4000-8000-000000000203'
    ) THEN
        RAISE EXCEPTION 'stale revision unexpectedly wrote a mutation receipt';
    END IF;

    -- Unrelated active Player cannot update an existing PC.
    SELECT count(*) INTO row_count
    FROM test_put_pc_snapshot(
        '10000000-0000-4000-8000-000000000203',
        '30000000-0000-4000-8000-000000000204',
        '40000000-0000-4000-8000-000000000201',
        '20000000-0000-4000-8000-000000000201',
        1,
        'Unauthorized Attempt',
        'dnd-custom-aid.character-backup',
        2,
        '{"format":"dnd-custom-aid.character-backup","version":2,"marker":"unauthorized"}'::jsonb
    );
    IF row_count <> 0 THEN
        RAISE EXCEPTION 'unrelated Player unexpectedly updated a PC';
    END IF;

    -- Kicked Player cannot create a new hosted PC either.
    SELECT count(*) INTO row_count
    FROM test_put_pc_snapshot(
        '10000000-0000-4000-8000-000000000204',
        '30000000-0000-4000-8000-000000000205',
        '40000000-0000-4000-8000-000000000202',
        '20000000-0000-4000-8000-000000000201',
        0,
        'Kicked Attempt',
        'dnd-custom-aid.character-backup',
        2,
        '{"format":"dnd-custom-aid.character-backup","version":2}'::jsonb
    );
    IF row_count <> 0 THEN
        RAISE EXCEPTION 'kicked Player unexpectedly created hosted PC state';
    END IF;

    -- DM may update any PC in the active campaign when the revision matches.
    SELECT * INTO r
    FROM test_put_pc_snapshot(
        '10000000-0000-4000-8000-000000000202',
        '30000000-0000-4000-8000-000000000206',
        '40000000-0000-4000-8000-000000000201',
        '20000000-0000-4000-8000-000000000201',
        1,
        'DM Correction',
        'dnd-custom-aid.character-backup',
        2,
        '{"format":"dnd-custom-aid.character-backup","version":2,"marker":"dm-update"}'::jsonb
    );
    IF NOT FOUND OR r.revision <> 2 OR r.name <> 'DM Correction' THEN
        RAISE EXCEPTION 'DM correction did not update campaign PC: %', r;
    END IF;
END
$$;

-- Verify the read projection: DM sees campaign PC; unrelated Player does not; owner does.
DO $$
DECLARE
    dm_count integer;
    owner_count integer;
    other_count integer;
BEGIN
    SELECT count(*) INTO dm_count
    FROM pc p
    JOIN campaign c ON c.id = p.campaign_id
    JOIN campaign_membership m ON m.campaign_id = p.campaign_id
    WHERE p.campaign_id = '20000000-0000-4000-8000-000000000201'::uuid
      AND m.user_id = '10000000-0000-4000-8000-000000000202'::uuid
      AND m.status = 'ACTIVE'
      AND c.deleted_at IS NULL
      AND p.snapshot IS NOT NULL
      AND p.snapshot_format IS NOT NULL
      AND p.snapshot_version IS NOT NULL
      AND (m.role = 'DM' OR p.owner_user_id = m.user_id OR p.controller_user_id = m.user_id);

    SELECT count(*) INTO owner_count
    FROM pc p
    JOIN campaign c ON c.id = p.campaign_id
    JOIN campaign_membership m ON m.campaign_id = p.campaign_id
    WHERE p.campaign_id = '20000000-0000-4000-8000-000000000201'::uuid
      AND m.user_id = '10000000-0000-4000-8000-000000000201'::uuid
      AND m.status = 'ACTIVE'
      AND c.deleted_at IS NULL
      AND p.snapshot IS NOT NULL
      AND p.snapshot_format IS NOT NULL
      AND p.snapshot_version IS NOT NULL
      AND (m.role = 'DM' OR p.owner_user_id = m.user_id OR p.controller_user_id = m.user_id);

    SELECT count(*) INTO other_count
    FROM pc p
    JOIN campaign c ON c.id = p.campaign_id
    JOIN campaign_membership m ON m.campaign_id = p.campaign_id
    WHERE p.campaign_id = '20000000-0000-4000-8000-000000000201'::uuid
      AND m.user_id = '10000000-0000-4000-8000-000000000203'::uuid
      AND m.status = 'ACTIVE'
      AND c.deleted_at IS NULL
      AND p.snapshot IS NOT NULL
      AND p.snapshot_format IS NOT NULL
      AND p.snapshot_version IS NOT NULL
      AND (m.role = 'DM' OR p.owner_user_id = m.user_id OR p.controller_user_id = m.user_id);

    IF dm_count <> 1 OR owner_count <> 1 OR other_count <> 0 THEN
        RAISE EXCEPTION 'PC read authorization projection failed: dm %, owner %, other %', dm_count, owner_count, other_count;
    END IF;
END
$$;
