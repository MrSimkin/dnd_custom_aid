\set ON_ERROR_STOP on

-- Campaign Administration membership lifecycle contract.
--
-- This proves the deliberately small DM moderation state machine used by the hosted API:
-- - only an ACTIVE DM can see/administer the campaign roster;
-- - this package moderates PLAYER memberships only;
-- - KICK never weakens an existing BAN;
-- - LIFT_BAN removes the ban but leaves the user KICKED so a later valid invite/rejoin flow owns
--   the transition back to ACTIVE;
-- - repeated actions are idempotent and do not inflate campaign revision;
-- - moderation does not delete membership rows or rewrite/delete PCs.

INSERT INTO app_user(id, descope_subject, display_name)
VALUES
    ('10000000-0000-4000-8000-000000000601', 'campaign-admin-dm', 'Campaign Admin DM'),
    ('10000000-0000-4000-8000-000000000602', 'campaign-admin-player', 'Campaign Admin Player'),
    ('10000000-0000-4000-8000-000000000603', 'campaign-admin-observer', 'Campaign Observer');

INSERT INTO campaign(id, name, revision)
VALUES ('20000000-0000-4000-8000-000000000601', 'Campaign Administration Contract', 5);

INSERT INTO campaign_membership(campaign_id, user_id, role, status)
VALUES
    ('20000000-0000-4000-8000-000000000601', '10000000-0000-4000-8000-000000000601', 'DM', 'ACTIVE'),
    ('20000000-0000-4000-8000-000000000601', '10000000-0000-4000-8000-000000000602', 'PLAYER', 'ACTIVE'),
    ('20000000-0000-4000-8000-000000000601', '10000000-0000-4000-8000-000000000603', 'PLAYER', 'ACTIVE');

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
    '40000000-0000-4000-8000-000000000601',
    '20000000-0000-4000-8000-000000000601',
    '10000000-0000-4000-8000-000000000602',
    '10000000-0000-4000-8000-000000000602',
    'Preserved Moderated PC',
    9,
    'dnd-custom-aid.character-backup',
    2,
    '{"format":"dnd-custom-aid.character-backup","version":2,"character":{"id":"40000000-0000-4000-8000-000000000601","campaignId":"20000000-0000-4000-8000-000000000601","name":"Preserved Moderated PC"}}'::jsonb,
    now()
);

-- SQL equivalent of the store transition, isolated in pg_temp so the test leaves no product schema
-- object behind. Advisory serialization is tested by the Worker implementation; this function tests
-- the relational state transition itself.
CREATE OR REPLACE FUNCTION pg_temp.moderate_membership(
    actor_id uuid,
    requested_campaign_id uuid,
    target_id uuid,
    requested_action text
)
RETURNS TABLE(result_status text, result_campaign_revision bigint, result_applied boolean)
LANGUAGE sql
AS $$
    WITH authorized_dm AS (
        SELECT 1
        FROM campaign_membership actor
        JOIN campaign c ON c.id = actor.campaign_id
        WHERE actor.campaign_id = requested_campaign_id
          AND actor.user_id = actor_id
          AND actor.role = 'DM'
          AND actor.status = 'ACTIVE'
          AND c.deleted_at IS NULL
    ),
    target AS (
        SELECT
            member.campaign_id,
            member.user_id,
            member.role,
            member.status AS current_status
        FROM campaign_membership member
        WHERE member.campaign_id = requested_campaign_id
          AND member.user_id = target_id
          AND member.role = 'PLAYER'
          AND EXISTS (SELECT 1 FROM authorized_dm)
    ),
    desired AS (
        SELECT
            target.*,
            CASE
                WHEN requested_action = 'KICK' AND target.current_status = 'ACTIVE' THEN 'KICKED'
                WHEN requested_action = 'BAN' AND target.current_status IN ('ACTIVE', 'KICKED') THEN 'BANNED'
                WHEN requested_action = 'LIFT_BAN' AND target.current_status = 'BANNED' THEN 'KICKED'
                ELSE target.current_status
            END AS desired_status
        FROM target
    ),
    updated_membership AS (
        UPDATE campaign_membership member
        SET status = desired.desired_status,
            updated_at = now()
        FROM desired
        WHERE member.campaign_id = desired.campaign_id
          AND member.user_id = desired.user_id
          AND member.status <> desired.desired_status
        RETURNING member.status
    ),
    updated_campaign AS (
        UPDATE campaign c
        SET revision = c.revision + 1,
            updated_at = now()
        WHERE c.id = requested_campaign_id
          AND EXISTS (SELECT 1 FROM updated_membership)
        RETURNING c.revision
    )
    SELECT
        changed.status::text,
        campaign_revision.revision,
        true
    FROM updated_membership changed
    CROSS JOIN updated_campaign campaign_revision
    UNION ALL
    SELECT
        desired.current_status::text,
        c.revision,
        false
    FROM desired
    JOIN campaign c ON c.id = desired.campaign_id
    WHERE NOT EXISTS (SELECT 1 FROM updated_membership);
$$;

DO $$
DECLARE
    row_count integer;
    current_status text;
    current_revision bigint;
    was_applied boolean;
    current_pc record;
BEGIN
    -- Active DM sees every membership, including inactive lifecycle rows when they later exist.
    WITH authorized_dm AS (
        SELECT 1
        FROM campaign_membership actor
        JOIN campaign c ON c.id = actor.campaign_id
        WHERE actor.campaign_id = '20000000-0000-4000-8000-000000000601'::uuid
          AND actor.user_id = '10000000-0000-4000-8000-000000000601'::uuid
          AND actor.role = 'DM'
          AND actor.status = 'ACTIVE'
          AND c.deleted_at IS NULL
    )
    SELECT count(*) INTO row_count
    FROM campaign_membership member
    WHERE member.campaign_id = '20000000-0000-4000-8000-000000000601'::uuid
      AND EXISTS (SELECT 1 FROM authorized_dm);
    IF row_count <> 3 THEN
        RAISE EXCEPTION 'active DM roster projection expected 3 members, got %', row_count;
    END IF;

    -- An ordinary ACTIVE Player cannot enumerate the campaign roster.
    WITH authorized_dm AS (
        SELECT 1
        FROM campaign_membership actor
        JOIN campaign c ON c.id = actor.campaign_id
        WHERE actor.campaign_id = '20000000-0000-4000-8000-000000000601'::uuid
          AND actor.user_id = '10000000-0000-4000-8000-000000000603'::uuid
          AND actor.role = 'DM'
          AND actor.status = 'ACTIVE'
          AND c.deleted_at IS NULL
    )
    SELECT count(*) INTO row_count
    FROM campaign_membership member
    WHERE member.campaign_id = '20000000-0000-4000-8000-000000000601'::uuid
      AND EXISTS (SELECT 1 FROM authorized_dm);
    IF row_count <> 0 THEN
        RAISE EXCEPTION 'ordinary Player unexpectedly received Campaign Administration roster';
    END IF;

    -- ACTIVE -> KICKED, campaign revision 5 -> 6.
    SELECT result_status, result_campaign_revision, result_applied
    INTO current_status, current_revision, was_applied
    FROM pg_temp.moderate_membership(
        '10000000-0000-4000-8000-000000000601'::uuid,
        '20000000-0000-4000-8000-000000000601'::uuid,
        '10000000-0000-4000-8000-000000000602'::uuid,
        'KICK'
    );
    IF current_status <> 'KICKED' OR current_revision <> 6 OR NOT was_applied THEN
        RAISE EXCEPTION 'ACTIVE -> KICKED produced unexpected result: %, %, %', current_status, current_revision, was_applied;
    END IF;

    -- Repeated KICK is idempotent and does not inflate revision.
    SELECT result_status, result_campaign_revision, result_applied
    INTO current_status, current_revision, was_applied
    FROM pg_temp.moderate_membership(
        '10000000-0000-4000-8000-000000000601'::uuid,
        '20000000-0000-4000-8000-000000000601'::uuid,
        '10000000-0000-4000-8000-000000000602'::uuid,
        'KICK'
    );
    IF current_status <> 'KICKED' OR current_revision <> 6 OR was_applied THEN
        RAISE EXCEPTION 'repeated KICK was not idempotent: %, %, %', current_status, current_revision, was_applied;
    END IF;

    -- KICKED -> BANNED, revision 6 -> 7.
    SELECT result_status, result_campaign_revision, result_applied
    INTO current_status, current_revision, was_applied
    FROM pg_temp.moderate_membership(
        '10000000-0000-4000-8000-000000000601'::uuid,
        '20000000-0000-4000-8000-000000000601'::uuid,
        '10000000-0000-4000-8000-000000000602'::uuid,
        'BAN'
    );
    IF current_status <> 'BANNED' OR current_revision <> 7 OR NOT was_applied THEN
        RAISE EXCEPTION 'KICKED -> BANNED produced unexpected result: %, %, %', current_status, current_revision, was_applied;
    END IF;

    -- KICK must never weaken BANNED -> KICKED. It is a no-op while banned.
    SELECT result_status, result_campaign_revision, result_applied
    INTO current_status, current_revision, was_applied
    FROM pg_temp.moderate_membership(
        '10000000-0000-4000-8000-000000000601'::uuid,
        '20000000-0000-4000-8000-000000000601'::uuid,
        '10000000-0000-4000-8000-000000000602'::uuid,
        'KICK'
    );
    IF current_status <> 'BANNED' OR current_revision <> 7 OR was_applied THEN
        RAISE EXCEPTION 'KICK unexpectedly weakened a BAN: %, %, %', current_status, current_revision, was_applied;
    END IF;

    -- Lift Ban deliberately returns BANNED -> KICKED, not ACTIVE. A later valid invite/rejoin flow
    -- owns KICKED -> ACTIVE according to the approved product contract.
    SELECT result_status, result_campaign_revision, result_applied
    INTO current_status, current_revision, was_applied
    FROM pg_temp.moderate_membership(
        '10000000-0000-4000-8000-000000000601'::uuid,
        '20000000-0000-4000-8000-000000000601'::uuid,
        '10000000-0000-4000-8000-000000000602'::uuid,
        'LIFT_BAN'
    );
    IF current_status <> 'KICKED' OR current_revision <> 8 OR NOT was_applied THEN
        RAISE EXCEPTION 'BANNED -> KICKED lift-ban produced unexpected result: %, %, %', current_status, current_revision, was_applied;
    END IF;

    -- Repeated Lift Ban is also idempotent.
    SELECT result_status, result_campaign_revision, result_applied
    INTO current_status, current_revision, was_applied
    FROM pg_temp.moderate_membership(
        '10000000-0000-4000-8000-000000000601'::uuid,
        '20000000-0000-4000-8000-000000000601'::uuid,
        '10000000-0000-4000-8000-000000000602'::uuid,
        'LIFT_BAN'
    );
    IF current_status <> 'KICKED' OR current_revision <> 8 OR was_applied THEN
        RAISE EXCEPTION 'repeated LIFT_BAN was not idempotent: %, %, %', current_status, current_revision, was_applied;
    END IF;

    -- A Player cannot moderate another Player; the generic store path has no result and therefore
    -- becomes HostedAuthorizationError at the backend boundary.
    SELECT count(*) INTO row_count
    FROM pg_temp.moderate_membership(
        '10000000-0000-4000-8000-000000000603'::uuid,
        '20000000-0000-4000-8000-000000000601'::uuid,
        '10000000-0000-4000-8000-000000000602'::uuid,
        'BAN'
    );
    IF row_count <> 0 THEN
        RAISE EXCEPTION 'ordinary Player unexpectedly obtained moderation authority';
    END IF;

    SELECT revision INTO current_revision
    FROM campaign
    WHERE id = '20000000-0000-4000-8000-000000000601'::uuid;
    IF current_revision <> 8 THEN
        RAISE EXCEPTION 'unauthorized/no-op actions unexpectedly changed campaign revision: %', current_revision;
    END IF;

    -- Membership row and PC authority metadata/data survive all moderation transitions unchanged.
    SELECT count(*) INTO row_count
    FROM campaign_membership
    WHERE campaign_id = '20000000-0000-4000-8000-000000000601'::uuid
      AND user_id = '10000000-0000-4000-8000-000000000602'::uuid;
    IF row_count <> 1 THEN
        RAISE EXCEPTION 'moderation deleted the membership row';
    END IF;

    SELECT revision, name, owner_user_id, controller_user_id, deleted_at
    INTO current_pc
    FROM pc
    WHERE id = '40000000-0000-4000-8000-000000000601'::uuid;
    IF NOT FOUND
       OR current_pc.revision <> 9
       OR current_pc.name <> 'Preserved Moderated PC'
       OR current_pc.owner_user_id <> '10000000-0000-4000-8000-000000000602'::uuid
       OR current_pc.controller_user_id <> '10000000-0000-4000-8000-000000000602'::uuid
       OR current_pc.deleted_at IS NOT NULL THEN
        RAISE EXCEPTION 'moderation mutated/deleted PC state or ownership/control: %', current_pc;
    END IF;
END $$;
