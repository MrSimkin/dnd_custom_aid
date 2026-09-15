\set ON_ERROR_STOP on

-- A DM is authorized to create/correct campaign PC state, but DM campaign authority alone must
-- not manufacture PC ownership/control. Explicit PC Manager assignment remains a separate action.

INSERT INTO app_user(id, descope_subject, display_name)
VALUES ('10000000-0000-4000-8000-000000000301', 'pc-dm-authority-test', 'DM Authority Test');

INSERT INTO campaign(id, name, revision)
VALUES ('20000000-0000-4000-8000-000000000301', 'DM Authority Campaign', 0);

INSERT INTO campaign_membership(campaign_id, user_id, role, status)
VALUES (
    '20000000-0000-4000-8000-000000000301',
    '10000000-0000-4000-8000-000000000301',
    'DM',
    'ACTIVE'
);

WITH membership AS (
    SELECT m.role
    FROM campaign_membership m
    JOIN campaign c ON c.id = m.campaign_id
    WHERE m.campaign_id = '20000000-0000-4000-8000-000000000301'::uuid
      AND m.user_id = '10000000-0000-4000-8000-000000000301'::uuid
      AND m.status = 'ACTIVE'
      AND c.deleted_at IS NULL
),
created_pc AS (
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
    SELECT
        '40000000-0000-4000-8000-000000000301'::uuid,
        '20000000-0000-4000-8000-000000000301'::uuid,
        CASE WHEN (SELECT role FROM membership) = 'PLAYER'
             THEN '10000000-0000-4000-8000-000000000301'::uuid ELSE NULL END,
        CASE WHEN (SELECT role FROM membership) = 'PLAYER'
             THEN '10000000-0000-4000-8000-000000000301'::uuid ELSE NULL END,
        'DM-created PC',
        0,
        'dnd-custom-aid.character-backup',
        2,
        '{"format":"dnd-custom-aid.character-backup","version":2}'::jsonb,
        now()
    WHERE EXISTS (SELECT 1 FROM membership)
    RETURNING owner_user_id, controller_user_id
)
SELECT 1 FROM created_pc;

DO $$
DECLARE
    p record;
BEGIN
    SELECT owner_user_id, controller_user_id
    INTO p
    FROM pc
    WHERE id = '40000000-0000-4000-8000-000000000301'::uuid;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'DM-authorized PC create was not persisted';
    END IF;
    IF p.owner_user_id IS NOT NULL OR p.controller_user_id IS NOT NULL THEN
        RAISE EXCEPTION 'DM authority unexpectedly manufactured PC ownership/control: %', p;
    END IF;
END
$$;
