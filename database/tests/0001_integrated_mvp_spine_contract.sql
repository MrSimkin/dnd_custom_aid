\set ON_ERROR_STOP on

-- This test runs against a disposable PostgreSQL service in CI after the real migration.
-- It protects the hosted persistence invariants without requiring a Neon account.

INSERT INTO app_user(id, descope_subject, display_name)
VALUES ('10000000-0000-4000-8000-000000000001', 'descope-owner', 'Owner');

DO $$
BEGIN
    BEGIN
        INSERT INTO app_user(id, descope_subject)
        VALUES ('10000000-0000-4000-8000-000000000002', '   ');
        RAISE EXCEPTION 'blank Descope subject unexpectedly accepted';
    EXCEPTION WHEN check_violation THEN
        NULL;
    END;

    BEGIN
        INSERT INTO campaign(id, name)
        VALUES ('20000000-0000-4000-8000-000000000001', '   ');
        RAISE EXCEPTION 'blank campaign name unexpectedly accepted';
    EXCEPTION WHEN check_violation THEN
        NULL;
    END;
END
$$;

-- Mirror the production create-campaign mutation so PostgreSQL itself validates the CTE,
-- idempotency receipt, membership and authorization assumptions used by the Worker store.
CREATE OR REPLACE FUNCTION test_create_campaign(
    p_user uuid,
    p_mutation uuid,
    p_campaign uuid,
    p_name text
)
RETURNS TABLE(id uuid, name text, role text, revision bigint, created boolean)
LANGUAGE plpgsql
AS $$
DECLARE
    lock_key text := p_user::text || ':' || p_mutation::text;
BEGIN
    PERFORM pg_advisory_xact_lock(hashtextextended(lock_key, 0));

    RETURN QUERY
    WITH existing_receipt AS (
        SELECT object_type, object_id, resulting_revision
        FROM mutation_receipt
        WHERE user_id = p_user
          AND mutation_id = p_mutation
    ),
    created_campaign AS (
        INSERT INTO campaign(id, name, revision)
        SELECT p_campaign, btrim(p_name), 0
        WHERE NOT EXISTS (SELECT 1 FROM existing_receipt)
        ON CONFLICT(id) DO NOTHING
        RETURNING campaign.id, campaign.name, campaign.revision
    ),
    new_receipt AS (
        INSERT INTO mutation_receipt(
            user_id, mutation_id, object_type, object_id, resulting_revision
        )
        SELECT p_user, p_mutation, 'CAMPAIGN', c.id, c.revision
        FROM created_campaign c
        RETURNING object_type, object_id, resulting_revision
    ),
    target AS (
        SELECT object_type, object_id, resulting_revision FROM existing_receipt
        UNION ALL
        SELECT object_type, object_id, resulting_revision FROM new_receipt
    ),
    target_campaign AS (
        SELECT c.id, c.name, c.revision
        FROM target t
        JOIN campaign c ON c.id = t.object_id
        WHERE t.object_type = 'CAMPAIGN'
          AND t.object_id = p_campaign
          AND c.deleted_at IS NULL
    ),
    new_membership AS (
        INSERT INTO campaign_membership(campaign_id, user_id, role, status)
        SELECT c.id, p_user, 'DM', 'ACTIVE'
        FROM created_campaign c
        RETURNING campaign_id, campaign_membership.role
    ),
    permitted_campaign AS (
        SELECT campaign_id, new_membership.role FROM new_membership
        UNION ALL
        SELECT m.campaign_id, m.role
        FROM campaign_membership m
        WHERE m.user_id = p_user
          AND m.status = 'ACTIVE'
          AND NOT EXISTS (SELECT 1 FROM created_campaign)
    )
    SELECT
        c.id,
        c.name,
        p.role,
        c.revision,
        EXISTS (SELECT 1 FROM created_campaign) AS created
    FROM target_campaign c
    JOIN permitted_campaign p ON p.campaign_id = c.id;
END
$$;

DO $$
DECLARE
    r record;
    row_count integer;
BEGIN
    SELECT * INTO r
    FROM test_create_campaign(
        '10000000-0000-4000-8000-000000000001',
        '30000000-0000-4000-8000-000000000001',
        '20000000-0000-4000-8000-000000000010',
        '  Terramore  '
    );

    IF NOT FOUND OR r.created IS DISTINCT FROM true THEN
        RAISE EXCEPTION 'first campaign mutation was not created';
    END IF;
    IF r.name <> 'Terramore' OR r.role <> 'DM' OR r.revision <> 0 THEN
        RAISE EXCEPTION 'first campaign mutation returned unexpected state: %', r;
    END IF;

    SELECT count(*) INTO row_count
    FROM campaign_membership
    WHERE campaign_id = '20000000-0000-4000-8000-000000000010'
      AND user_id = '10000000-0000-4000-8000-000000000001'
      AND role = 'DM'
      AND status = 'ACTIVE';
    IF row_count <> 1 THEN
        RAISE EXCEPTION 'creator DM membership missing';
    END IF;

    SELECT * INTO r
    FROM test_create_campaign(
        '10000000-0000-4000-8000-000000000001',
        '30000000-0000-4000-8000-000000000001',
        '20000000-0000-4000-8000-000000000010',
        'Terramore'
    );
    IF NOT FOUND OR r.created IS DISTINCT FROM false THEN
        RAISE EXCEPTION 'idempotent retry did not return existing campaign';
    END IF;

    SELECT count(*) INTO row_count
    FROM test_create_campaign(
        '10000000-0000-4000-8000-000000000001',
        '30000000-0000-4000-8000-000000000001',
        '20000000-0000-4000-8000-000000000011',
        'Wrong target'
    );
    IF row_count <> 0 THEN
        RAISE EXCEPTION 'mutation id reuse unexpectedly targeted a different campaign';
    END IF;
    IF EXISTS (
        SELECT 1 FROM campaign
        WHERE id = '20000000-0000-4000-8000-000000000011'
    ) THEN
        RAISE EXCEPTION 'mutation id reuse created a different campaign';
    END IF;
END
$$;

-- A fresh mutation that collides with an existing campaign UUID must neither claim it nor
-- manufacture an idempotency receipt for it.
INSERT INTO campaign(id, name)
VALUES ('20000000-0000-4000-8000-000000000020', 'Existing foreign campaign');

DO $$
DECLARE
    row_count integer;
BEGIN
    SELECT count(*) INTO row_count
    FROM test_create_campaign(
        '10000000-0000-4000-8000-000000000001',
        '30000000-0000-4000-8000-000000000020',
        '20000000-0000-4000-8000-000000000020',
        'Collision'
    );
    IF row_count <> 0 THEN
        RAISE EXCEPTION 'campaign UUID collision unexpectedly returned a campaign';
    END IF;

    IF EXISTS (
        SELECT 1 FROM campaign_membership
        WHERE campaign_id = '20000000-0000-4000-8000-000000000020'
          AND user_id = '10000000-0000-4000-8000-000000000001'
    ) THEN
        RAISE EXCEPTION 'campaign UUID collision granted membership';
    END IF;

    IF EXISTS (
        SELECT 1 FROM mutation_receipt
        WHERE user_id = '10000000-0000-4000-8000-000000000001'
          AND mutation_id = '30000000-0000-4000-8000-000000000020'
    ) THEN
        RAISE EXCEPTION 'campaign UUID collision wrote a mutation receipt';
    END IF;
END
$$;

-- PC metadata keeps ownership/control independent and enforces snapshot-version sanity.
INSERT INTO app_user(id, descope_subject, display_name)
VALUES ('10000000-0000-4000-8000-000000000003', 'descope-controller', 'Controller');

INSERT INTO campaign_membership(campaign_id, user_id, role, status)
VALUES
    ('20000000-0000-4000-8000-000000000010', '10000000-0000-4000-8000-000000000003', 'PLAYER', 'ACTIVE');

INSERT INTO pc(
    id, campaign_id, owner_user_id, controller_user_id, name,
    snapshot_format, snapshot_version, snapshot
)
VALUES (
    '40000000-0000-4000-8000-000000000001',
    '20000000-0000-4000-8000-000000000010',
    '10000000-0000-4000-8000-000000000001',
    '10000000-0000-4000-8000-000000000003',
    'Vanya',
    'dnd-custom-aid-pc',
    1,
    '{"name":"Vanya"}'::jsonb
);

DO $$
DECLARE
    owner_id uuid;
    controller_id uuid;
BEGIN
    SELECT owner_user_id, controller_user_id
    INTO owner_id, controller_id
    FROM pc
    WHERE id = '40000000-0000-4000-8000-000000000001';

    IF owner_id = controller_id THEN
        RAISE EXCEPTION 'PC owner and controller unexpectedly collapsed into one identity';
    END IF;

    BEGIN
        INSERT INTO pc(id, campaign_id, name, snapshot_version)
        VALUES (
            '40000000-0000-4000-8000-000000000002',
            '20000000-0000-4000-8000-000000000010',
            'Invalid snapshot',
            0
        );
        RAISE EXCEPTION 'invalid snapshot version unexpectedly accepted';
    EXCEPTION WHEN check_violation THEN
        NULL;
    END;
END
$$;

DROP FUNCTION test_create_campaign(uuid, uuid, uuid, text);
