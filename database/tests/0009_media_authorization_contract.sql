\set ON_ERROR_STOP on

-- Base para DM, package 4: who may read a handout image. Mirrors NeonMediaAuthorization.mayReadMedia
-- and isActiveDm (backend/src/mediaStore.ts) against the dm_content rules of migration 0002.

INSERT INTO app_user(id, descope_subject, display_name)
VALUES
    ('10000000-0000-4000-8000-000000000901', 'media-dm', 'Media DM'),
    ('10000000-0000-4000-8000-000000000902', 'media-player', 'Media Player');

INSERT INTO campaign(id, name, revision)
VALUES ('20000000-0000-4000-8000-000000000901', 'Media Campaign', 0);

INSERT INTO campaign_membership(campaign_id, user_id, role, status)
VALUES
    ('20000000-0000-4000-8000-000000000901', '10000000-0000-4000-8000-000000000901', 'DM', 'ACTIVE'),
    ('20000000-0000-4000-8000-000000000901', '10000000-0000-4000-8000-000000000902', 'PLAYER', 'ACTIVE');

SELECT * FROM dm_content_put(
    '10000000-0000-4000-8000-000000000901', '30000000-0000-4000-8000-000000000901',
    '50000000-0000-4000-8000-000000000901', 0, 'CAMPAIGN', '20000000-0000-4000-8000-000000000901',
    'HANDOUT', 'Mapa', 'dnd-custom-aid.dm-content', 1,
    jsonb_build_object('payload', jsonb_build_object('sha256', repeat('a', 64), 'mediaType', 'image/png'))
);

CREATE OR REPLACE FUNCTION test_may_read_media(p_actor uuid, p_content uuid, p_sha text)
RETURNS boolean
LANGUAGE sql
AS $$
    SELECT EXISTS (
        SELECT 1
        FROM dm_content d
        WHERE d.id = p_content
          AND d.family = 'HANDOUT'
          AND d.deleted_at IS NULL
          AND d.snapshot -> 'payload' ->> 'sha256' = p_sha
          AND dm_content_actor_may_write(p_actor, d.scope_kind, d.owner_user_id, d.campaign_id)
    )
$$;

CREATE OR REPLACE FUNCTION test_is_active_dm(p_actor uuid)
RETURNS boolean
LANGUAGE sql
AS $$
    SELECT EXISTS (
        SELECT 1
        FROM campaign_membership m
        JOIN campaign c ON c.id = m.campaign_id
        WHERE m.user_id = p_actor AND m.role = 'DM' AND m.status = 'ACTIVE' AND c.deleted_at IS NULL
    )
$$;

DO $$
DECLARE
    dm constant uuid := '10000000-0000-4000-8000-000000000901';
    player constant uuid := '10000000-0000-4000-8000-000000000902';
    handout constant uuid := '50000000-0000-4000-8000-000000000901';
BEGIN
    IF NOT test_is_active_dm(dm) OR test_is_active_dm(player) THEN
        RAISE EXCEPTION 'only the active DM may upload images';
    END IF;
    IF NOT test_may_read_media(dm, handout, repeat('a', 64)) THEN
        RAISE EXCEPTION 'the DM cannot read the image of its own handout';
    END IF;
    IF test_may_read_media(player, handout, repeat('a', 64)) THEN
        RAISE EXCEPTION 'a player read a DM handout image';
    END IF;
    IF test_may_read_media(dm, handout, repeat('b', 64)) THEN
        RAISE EXCEPTION 'a handout unlocked an image it does not name';
    END IF;

    PERFORM dm_content_delete(dm, '30000000-0000-4000-8000-000000000902', handout, 0);
    IF test_may_read_media(dm, handout, repeat('a', 64)) THEN
        RAISE EXCEPTION 'a deleted handout still unlocks its image';
    END IF;

    UPDATE campaign_membership SET status = 'KICKED'
    WHERE campaign_id = '20000000-0000-4000-8000-000000000901' AND user_id = dm;
    IF test_is_active_dm(dm) THEN
        RAISE EXCEPTION 'a kicked DM may still upload images';
    END IF;
END
$$;

DROP FUNCTION test_may_read_media(uuid, uuid, text);
DROP FUNCTION test_is_active_dm(uuid);
