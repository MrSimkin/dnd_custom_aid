\set ON_ERROR_STOP on

-- Base para DM, package 1: hosted DM content contract. Exercises the production functions from
-- migration 0002 directly (dm_content_put / dm_content_delete / dm_content_list_*), so the Worker
-- and this proof share one implementation.

INSERT INTO app_user(id, descope_subject, display_name)
VALUES
    ('10000000-0000-4000-8000-000000000801', 'content-dm', 'Content DM'),
    ('10000000-0000-4000-8000-000000000802', 'content-player', 'Content Player'),
    ('10000000-0000-4000-8000-000000000803', 'content-kicked-dm', 'Kicked DM'),
    ('10000000-0000-4000-8000-000000000804', 'content-outsider', 'Outsider');

INSERT INTO campaign(id, name, revision)
VALUES ('20000000-0000-4000-8000-000000000801', 'Content Campaign', 0);

INSERT INTO campaign_membership(campaign_id, user_id, role, status)
VALUES
    ('20000000-0000-4000-8000-000000000801', '10000000-0000-4000-8000-000000000801', 'DM', 'ACTIVE'),
    ('20000000-0000-4000-8000-000000000801', '10000000-0000-4000-8000-000000000802', 'PLAYER', 'ACTIVE'),
    ('20000000-0000-4000-8000-000000000801', '10000000-0000-4000-8000-000000000803', 'DM', 'KICKED');

-- Runs a statement and requires it to fail with the given private SQLSTATE.
CREATE OR REPLACE FUNCTION test_expect_dc(p_statement text, p_code text, p_label text)
RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
    BEGIN
        EXECUTE p_statement;
    EXCEPTION WHEN OTHERS THEN
        IF SQLSTATE <> p_code THEN
            RAISE EXCEPTION '%: expected SQLSTATE %, got % (%)', p_label, p_code, SQLSTATE, SQLERRM;
        END IF;
        RETURN;
    END;
    RAISE EXCEPTION '%: statement unexpectedly succeeded', p_label;
END;
$$;

DO $$
DECLARE
    r record;
    n integer;
    dm constant uuid := '10000000-0000-4000-8000-000000000801';
    player constant uuid := '10000000-0000-4000-8000-000000000802';
    camp constant uuid := '20000000-0000-4000-8000-000000000801';
    goblin constant uuid := '50000000-0000-4000-8000-000000000801';
    personal_rule constant uuid := '50000000-0000-4000-8000-000000000802';
    snap constant jsonb := '{"format":"dnd-custom-aid.dm-content","version":1,"content":{"id":"50000000-0000-4000-8000-000000000801"}}';
BEGIN
    -- The campaign DM creates campaign content at revision 0.
    SELECT * INTO r FROM dm_content_put(dm, '30000000-0000-4000-8000-000000000801', goblin, 0,
        'CAMPAIGN', camp, 'CREATURE', '  Goblin  ', 'dnd-custom-aid.dm-content', 1, snap);
    IF r.applied IS DISTINCT FROM true OR r.resulting_revision <> 0 THEN
        RAISE EXCEPTION 'DM create was not applied at revision 0: %', r;
    END IF;
    IF (SELECT display_name FROM dm_content WHERE id = goblin) <> 'Goblin' THEN
        RAISE EXCEPTION 'display name was not trimmed';
    END IF;

    -- Same mutation identity replays without another revision.
    SELECT * INTO r FROM dm_content_put(dm, '30000000-0000-4000-8000-000000000801', goblin, 0,
        'CAMPAIGN', camp, 'CREATURE', 'Goblin', 'dnd-custom-aid.dm-content', 1, snap);
    IF r.applied IS DISTINCT FROM false OR r.resulting_revision <> 0 THEN
        RAISE EXCEPTION 'replay was not idempotent: %', r;
    END IF;

    -- Update at the exact revision advances once.
    SELECT * INTO r FROM dm_content_put(dm, '30000000-0000-4000-8000-000000000802', goblin, 0,
        'CAMPAIGN', camp, 'CREATURE', 'Goblin jefe', 'dnd-custom-aid.dm-content', 1, snap);
    IF r.applied IS DISTINCT FROM true OR r.resulting_revision <> 1 THEN
        RAISE EXCEPTION 'update did not advance to revision 1: %', r;
    END IF;

    -- Personal content belongs only to its owner.
    SELECT * INTO r FROM dm_content_put(dm, '30000000-0000-4000-8000-000000000803', personal_rule, 0,
        'PERSONAL', dm, 'HOMEBREW_RULE', 'Pociones', 'dnd-custom-aid.dm-content', 1, snap);
    IF r.applied IS DISTINCT FROM true THEN
        RAISE EXCEPTION 'personal create was not applied';
    END IF;

    SELECT count(*) INTO n FROM dm_content_list_personal(dm);
    IF n <> 1 THEN RAISE EXCEPTION 'owner personal list returned % rows', n; END IF;
    SELECT count(*) INTO n FROM dm_content_list_personal(player);
    IF n <> 0 THEN RAISE EXCEPTION 'another account saw personal content'; END IF;
    SELECT count(*) INTO n FROM dm_content_list_campaign(dm, camp);
    IF n <> 1 THEN RAISE EXCEPTION 'DM campaign list returned % rows', n; END IF;
END
$$;

-- Authorization, staleness, identity and scope rules.
SELECT test_expect_dc($q$SELECT * FROM dm_content_list_campaign('10000000-0000-4000-8000-000000000802', '20000000-0000-4000-8000-000000000801')$q$,
    'DC403', 'player reading campaign DM content');
SELECT test_expect_dc($q$SELECT * FROM dm_content_list_campaign('10000000-0000-4000-8000-000000000803', '20000000-0000-4000-8000-000000000801')$q$,
    'DC403', 'kicked DM reading campaign DM content');
SELECT test_expect_dc($q$SELECT * FROM dm_content_put('10000000-0000-4000-8000-000000000802', '30000000-0000-4000-8000-000000000810', '50000000-0000-4000-8000-000000000801', 1, 'CAMPAIGN', '20000000-0000-4000-8000-000000000801', 'CREATURE', 'Hack', 'dnd-custom-aid.dm-content', 1, '{}'::jsonb)$q$,
    'DC403', 'player updating campaign DM content');
SELECT test_expect_dc($q$SELECT * FROM dm_content_put('10000000-0000-4000-8000-000000000802', '30000000-0000-4000-8000-000000000811', '50000000-0000-4000-8000-000000000809', 0, 'CAMPAIGN', '20000000-0000-4000-8000-000000000801', 'CREATURE', 'Hack', 'dnd-custom-aid.dm-content', 1, '{}'::jsonb)$q$,
    'DC403', 'player creating campaign DM content');
SELECT test_expect_dc($q$SELECT * FROM dm_content_put('10000000-0000-4000-8000-000000000802', '30000000-0000-4000-8000-000000000812', '50000000-0000-4000-8000-000000000808', 0, 'PERSONAL', '10000000-0000-4000-8000-000000000801', 'CREATURE', 'Hack', 'dnd-custom-aid.dm-content', 1, '{}'::jsonb)$q$,
    'DC403', 'creating personal content for someone else');
SELECT test_expect_dc($q$SELECT * FROM dm_content_put('10000000-0000-4000-8000-000000000801', '30000000-0000-4000-8000-000000000813', '50000000-0000-4000-8000-000000000801', 0, 'CAMPAIGN', '20000000-0000-4000-8000-000000000801', 'CREATURE', 'Old', 'dnd-custom-aid.dm-content', 1, '{}'::jsonb)$q$,
    'DC409', 'stale revision');
SELECT test_expect_dc($q$SELECT * FROM dm_content_put('10000000-0000-4000-8000-000000000801', '30000000-0000-4000-8000-000000000814', '50000000-0000-4000-8000-000000000801', 1, 'CAMPAIGN', '20000000-0000-4000-8000-000000000801', 'NPC', 'Goblin', 'dnd-custom-aid.dm-content', 1, '{}'::jsonb)$q$,
    'DC422', 'family change');
SELECT test_expect_dc($q$SELECT * FROM dm_content_put('10000000-0000-4000-8000-000000000801', '30000000-0000-4000-8000-000000000815', '50000000-0000-4000-8000-000000000801', 1, 'PERSONAL', '10000000-0000-4000-8000-000000000801', 'CREATURE', 'Goblin', 'dnd-custom-aid.dm-content', 1, '{}'::jsonb)$q$,
    'DC422', 'scope change');
SELECT test_expect_dc($q$SELECT * FROM dm_content_put('10000000-0000-4000-8000-000000000801', '30000000-0000-4000-8000-000000000816', '50000000-0000-4000-8000-000000000807', 3, 'CAMPAIGN', '20000000-0000-4000-8000-000000000801', 'CREATURE', 'Ghost', 'dnd-custom-aid.dm-content', 1, '{}'::jsonb)$q$,
    'DC404', 'update of unknown content');
SELECT test_expect_dc($q$SELECT * FROM dm_content_put('10000000-0000-4000-8000-000000000801', '30000000-0000-4000-8000-000000000801', '50000000-0000-4000-8000-000000000806', 0, 'CAMPAIGN', '20000000-0000-4000-8000-000000000801', 'CREATURE', 'Reuse', 'dnd-custom-aid.dm-content', 1, '{}'::jsonb)$q$,
    'DC412', 'mutation identity reused for another object');

DO $$
BEGIN
    IF (SELECT revision FROM dm_content WHERE id = '50000000-0000-4000-8000-000000000801') <> 1 THEN
        RAISE EXCEPTION 'a rejected mutation changed the content revision';
    END IF;
    IF EXISTS (SELECT 1 FROM dm_content WHERE id IN (
        '50000000-0000-4000-8000-000000000806', '50000000-0000-4000-8000-000000000807',
        '50000000-0000-4000-8000-000000000808', '50000000-0000-4000-8000-000000000809')) THEN
        RAISE EXCEPTION 'a rejected mutation created content';
    END IF;
END
$$;

-- Tombstones: stale delete fails, exact delete advances, nothing resurrects.
SELECT test_expect_dc($q$SELECT * FROM dm_content_delete('10000000-0000-4000-8000-000000000801', '30000000-0000-4000-8000-000000000820', '50000000-0000-4000-8000-000000000801', 0)$q$,
    'DC409', 'stale delete');
SELECT test_expect_dc($q$SELECT * FROM dm_content_delete('10000000-0000-4000-8000-000000000802', '30000000-0000-4000-8000-000000000821', '50000000-0000-4000-8000-000000000801', 1)$q$,
    'DC403', 'player delete');

DO $$
DECLARE
    r record;
    n integer;
BEGIN
    SELECT * INTO r FROM dm_content_delete('10000000-0000-4000-8000-000000000801',
        '30000000-0000-4000-8000-000000000822', '50000000-0000-4000-8000-000000000801', 1);
    IF r.applied IS DISTINCT FROM true OR r.resulting_revision <> 2 THEN
        RAISE EXCEPTION 'delete did not advance to revision 2: %', r;
    END IF;
    SELECT * INTO r FROM dm_content_delete('10000000-0000-4000-8000-000000000801',
        '30000000-0000-4000-8000-000000000822', '50000000-0000-4000-8000-000000000801', 1);
    IF r.applied IS DISTINCT FROM false OR r.resulting_revision <> 2 THEN
        RAISE EXCEPTION 'delete replay was not idempotent: %', r;
    END IF;
    SELECT count(*) INTO n
    FROM dm_content_list_campaign('10000000-0000-4000-8000-000000000801', '20000000-0000-4000-8000-000000000801')
    WHERE deleted_at IS NOT NULL;
    IF n <> 1 THEN
        RAISE EXCEPTION 'tombstone is not visible to the DM list';
    END IF;
END
$$;

SELECT test_expect_dc($q$SELECT * FROM dm_content_put('10000000-0000-4000-8000-000000000801', '30000000-0000-4000-8000-000000000823', '50000000-0000-4000-8000-000000000801', 2, 'CAMPAIGN', '20000000-0000-4000-8000-000000000801', 'CREATURE', 'Zombie goblin', 'dnd-custom-aid.dm-content', 1, '{}'::jsonb)$q$,
    'DC410', 'update after delete');
SELECT test_expect_dc($q$SELECT * FROM dm_content_put('10000000-0000-4000-8000-000000000801', '30000000-0000-4000-8000-000000000824', '50000000-0000-4000-8000-000000000801', 0, 'CAMPAIGN', '20000000-0000-4000-8000-000000000801', 'CREATURE', 'Zombie goblin', 'dnd-custom-aid.dm-content', 1, '{}'::jsonb)$q$,
    'DC410', 'recreate after delete');
SELECT test_expect_dc($q$SELECT * FROM dm_content_delete('10000000-0000-4000-8000-000000000801', '30000000-0000-4000-8000-000000000825', '50000000-0000-4000-8000-000000000801', 2)$q$,
    'DC410', 'delete twice');

DROP FUNCTION test_expect_dc(text, text, text);
