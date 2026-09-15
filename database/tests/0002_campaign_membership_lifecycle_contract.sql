\set ON_ERROR_STOP on

-- Validate the exact lifecycle shape consumed by the Worker store. This remains a contract test,
-- not a second migration: the existing campaign_membership status and campaign soft-delete fields
-- already carry the required semantics.

INSERT INTO app_user(id, descope_subject, display_name)
VALUES ('10000000-0000-4000-8000-000000000101', 'lifecycle-user', 'Lifecycle User');

INSERT INTO campaign(id, name, revision, deleted_at)
VALUES
    ('20000000-0000-4000-8000-000000000101', 'Removed Campaign', 7, to_timestamp(1234)),
    ('20000000-0000-4000-8000-000000000102', 'Active Campaign', 2, NULL);

INSERT INTO campaign_membership(campaign_id, user_id, role, status)
VALUES
    ('20000000-0000-4000-8000-000000000101', '10000000-0000-4000-8000-000000000101', 'PLAYER', 'KICKED'),
    ('20000000-0000-4000-8000-000000000102', '10000000-0000-4000-8000-000000000101', 'DM', 'ACTIVE');

DO $$
DECLARE
    lifecycle_row record;
    lifecycle_count integer;
    active_count integer;
BEGIN
    SELECT
        c.id::text AS campaign_id,
        c.name,
        m.role,
        m.status,
        c.revision::text AS revision,
        CASE
          WHEN c.deleted_at IS NULL THEN NULL
          ELSE floor(extract(epoch FROM c.deleted_at))::bigint::text
        END AS deleted_at_epoch_seconds
    INTO lifecycle_row
    FROM campaign_membership m
    JOIN campaign c ON c.id = m.campaign_id
    WHERE m.user_id = '10000000-0000-4000-8000-000000000101'::uuid
      AND c.id = '20000000-0000-4000-8000-000000000101'::uuid;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'inactive lifecycle row was not returned';
    END IF;
    IF lifecycle_row.status <> 'KICKED' OR lifecycle_row.role <> 'PLAYER' THEN
        RAISE EXCEPTION 'membership lifecycle fields were not preserved: %', lifecycle_row;
    END IF;
    IF lifecycle_row.revision <> '7' OR lifecycle_row.deleted_at_epoch_seconds <> '1234' THEN
        RAISE EXCEPTION 'campaign lifecycle metadata was not preserved: %', lifecycle_row;
    END IF;

    SELECT count(*) INTO lifecycle_count
    FROM campaign_membership m
    JOIN campaign c ON c.id = m.campaign_id
    WHERE m.user_id = '10000000-0000-4000-8000-000000000101'::uuid;
    IF lifecycle_count <> 2 THEN
        RAISE EXCEPTION 'lifecycle snapshot must include active and inactive memberships';
    END IF;

    SELECT count(*) INTO active_count
    FROM campaign_membership m
    JOIN campaign c ON c.id = m.campaign_id
    WHERE m.user_id = '10000000-0000-4000-8000-000000000101'::uuid
      AND m.status = 'ACTIVE'
      AND c.deleted_at IS NULL;
    IF active_count <> 1 THEN
        RAISE EXCEPTION 'active campaign projection no longer filters inactive/deleted memberships';
    END IF;
END
$$;
