\set ON_ERROR_STOP on

-- Explicit PC authority administration contract. Ownership and current controller are separate
-- from campaign DM role and from PC snapshot revision/content.

INSERT INTO app_user(id, descope_subject, display_name)
VALUES
    ('10000000-0000-4000-8000-000000000701', 'pc-authority-dm', 'Authority DM'),
    ('10000000-0000-4000-8000-000000000702', 'pc-authority-owner', 'Owner Candidate'),
    ('10000000-0000-4000-8000-000000000703', 'pc-authority-controller', 'Controller Candidate'),
    ('10000000-0000-4000-8000-000000000704', 'pc-authority-kicked', 'Kicked Candidate'),
    ('10000000-0000-4000-8000-000000000705', 'pc-authority-outsider', 'Other Campaign Player');

INSERT INTO campaign(id, name, revision)
VALUES
    ('20000000-0000-4000-8000-000000000701', 'Authority Campaign', 0),
    ('20000000-0000-4000-8000-000000000702', 'Other Authority Campaign', 0);

INSERT INTO campaign_membership(campaign_id, user_id, role, status)
VALUES
    ('20000000-0000-4000-8000-000000000701', '10000000-0000-4000-8000-000000000701', 'DM', 'ACTIVE'),
    ('20000000-0000-4000-8000-000000000701', '10000000-0000-4000-8000-000000000702', 'PLAYER', 'ACTIVE'),
    ('20000000-0000-4000-8000-000000000701', '10000000-0000-4000-8000-000000000703', 'PLAYER', 'ACTIVE'),
    ('20000000-0000-4000-8000-000000000701', '10000000-0000-4000-8000-000000000704', 'PLAYER', 'KICKED'),
    ('20000000-0000-4000-8000-000000000702', '10000000-0000-4000-8000-000000000705', 'PLAYER', 'ACTIVE');

INSERT INTO pc(
    id, campaign_id, owner_user_id, controller_user_id, name, revision,
    snapshot_format, snapshot_version, snapshot, reconciled_at
)
VALUES (
    '40000000-0000-4000-8000-000000000701',
    '20000000-0000-4000-8000-000000000701',
    NULL,
    NULL,
    'Authority Hero',
    5,
    'dnd-custom-aid.character-backup',
    2,
    '{"format":"dnd-custom-aid.character-backup","version":2,"marker":"authority-contract"}'::jsonb,
    now()
);

CREATE OR REPLACE FUNCTION test_set_pc_authority(
    p_actor uuid,
    p_pc uuid,
    p_campaign uuid,
    p_owner uuid,
    p_controller uuid
)
RETURNS TABLE(
    pc_id uuid,
    campaign_id uuid,
    owner_user_id uuid,
    controller_user_id uuid,
    revision bigint,
    snapshot jsonb,
    applied boolean
)
LANGUAGE sql
AS $$
    WITH actor AS (
        SELECT 1
        FROM campaign_membership m
        JOIN campaign c ON c.id = m.campaign_id
        WHERE m.campaign_id = p_campaign
          AND m.user_id = p_actor
          AND m.role = 'DM'
          AND m.status = 'ACTIVE'
          AND c.deleted_at IS NULL
    ),
    owner_allowed AS (
        SELECT 1
        WHERE p_owner IS NULL
           OR EXISTS (
                SELECT 1
                FROM campaign_membership m
                WHERE m.campaign_id = p_campaign
                  AND m.user_id = p_owner
                  AND m.status = 'ACTIVE'
           )
    ),
    controller_allowed AS (
        SELECT 1
        WHERE p_controller IS NULL
           OR EXISTS (
                SELECT 1
                FROM campaign_membership m
                WHERE m.campaign_id = p_campaign
                  AND m.user_id = p_controller
                  AND m.status = 'ACTIVE'
           )
    ),
    existing_pc AS (
        SELECT p.*
        FROM pc p
        JOIN campaign c ON c.id = p.campaign_id
        WHERE p.id = p_pc
          AND p.campaign_id = p_campaign
          AND p.deleted_at IS NULL
          AND c.deleted_at IS NULL
    ),
    updated_pc AS (
        UPDATE pc p
        SET owner_user_id = p_owner,
            controller_user_id = p_controller,
            updated_at = now()
        WHERE p.id = p_pc
          AND p.campaign_id = p_campaign
          AND p.deleted_at IS NULL
          AND EXISTS (SELECT 1 FROM actor)
          AND EXISTS (SELECT 1 FROM owner_allowed)
          AND EXISTS (SELECT 1 FROM controller_allowed)
          AND (
              p.owner_user_id IS DISTINCT FROM p_owner
              OR p.controller_user_id IS DISTINCT FROM p_controller
          )
        RETURNING p.*, true AS applied
    ),
    unchanged_pc AS (
        SELECT p.*, false AS applied
        FROM existing_pc p
        WHERE EXISTS (SELECT 1 FROM actor)
          AND EXISTS (SELECT 1 FROM owner_allowed)
          AND EXISTS (SELECT 1 FROM controller_allowed)
          AND NOT EXISTS (SELECT 1 FROM updated_pc)
    )
    SELECT
        result.id,
        result.campaign_id,
        result.owner_user_id,
        result.controller_user_id,
        result.revision,
        result.snapshot,
        result.applied
    FROM (
        SELECT * FROM updated_pc
        UNION ALL
        SELECT * FROM unchanged_pc
    ) result;
$$;

DO $$
DECLARE
    r record;
    row_count integer;
BEGIN
    -- Active DM assigns independent active same-campaign owner/controller.
    SELECT * INTO r
    FROM test_set_pc_authority(
        '10000000-0000-4000-8000-000000000701',
        '40000000-0000-4000-8000-000000000701',
        '20000000-0000-4000-8000-000000000701',
        '10000000-0000-4000-8000-000000000702',
        '10000000-0000-4000-8000-000000000703'
    );
    IF NOT FOUND OR r.applied IS DISTINCT FROM true THEN
        RAISE EXCEPTION 'DM authority assignment was not applied';
    END IF;
    IF r.owner_user_id <> '10000000-0000-4000-8000-000000000702'::uuid
       OR r.controller_user_id <> '10000000-0000-4000-8000-000000000703'::uuid THEN
        RAISE EXCEPTION 'owner/controller assignment returned unexpected authority: %', r;
    END IF;
    IF r.revision <> 5 OR r.snapshot->>'marker' <> 'authority-contract' THEN
        RAISE EXCEPTION 'authority assignment unexpectedly changed PC content revision/snapshot: %', r;
    END IF;

    -- Exact same replacement is an idempotent no-op.
    SELECT * INTO r
    FROM test_set_pc_authority(
        '10000000-0000-4000-8000-000000000701',
        '40000000-0000-4000-8000-000000000701',
        '20000000-0000-4000-8000-000000000701',
        '10000000-0000-4000-8000-000000000702',
        '10000000-0000-4000-8000-000000000703'
    );
    IF NOT FOUND OR r.applied IS DISTINCT FROM false OR r.revision <> 5 THEN
        RAISE EXCEPTION 'idempotent authority replacement unexpectedly mutated state: %', r;
    END IF;

    -- Explicit null clears both authority fields without touching content revision.
    SELECT * INTO r
    FROM test_set_pc_authority(
        '10000000-0000-4000-8000-000000000701',
        '40000000-0000-4000-8000-000000000701',
        '20000000-0000-4000-8000-000000000701',
        NULL,
        NULL
    );
    IF NOT FOUND OR r.applied IS DISTINCT FROM true
       OR r.owner_user_id IS NOT NULL OR r.controller_user_id IS NOT NULL
       OR r.revision <> 5 THEN
        RAISE EXCEPTION 'explicit authority unassignment failed: %', r;
    END IF;

    -- A Player cannot administer PC authority.
    SELECT count(*) INTO row_count
    FROM test_set_pc_authority(
        '10000000-0000-4000-8000-000000000702',
        '40000000-0000-4000-8000-000000000701',
        '20000000-0000-4000-8000-000000000701',
        '10000000-0000-4000-8000-000000000702',
        NULL
    );
    IF row_count <> 0 THEN
        RAISE EXCEPTION 'Player unexpectedly administered PC authority';
    END IF;

    -- Inactive same-campaign target is not eligible.
    SELECT count(*) INTO row_count
    FROM test_set_pc_authority(
        '10000000-0000-4000-8000-000000000701',
        '40000000-0000-4000-8000-000000000701',
        '20000000-0000-4000-8000-000000000701',
        '10000000-0000-4000-8000-000000000704',
        NULL
    );
    IF row_count <> 0 THEN
        RAISE EXCEPTION 'kicked member unexpectedly became PC owner';
    END IF;

    -- Active member of another campaign is not eligible.
    SELECT count(*) INTO row_count
    FROM test_set_pc_authority(
        '10000000-0000-4000-8000-000000000701',
        '40000000-0000-4000-8000-000000000701',
        '20000000-0000-4000-8000-000000000701',
        NULL,
        '10000000-0000-4000-8000-000000000705'
    );
    IF row_count <> 0 THEN
        RAISE EXCEPTION 'cross-campaign member unexpectedly became PC controller';
    END IF;
END
$$;
