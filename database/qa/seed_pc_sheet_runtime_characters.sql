\set ON_ERROR_STOP on

-- DEV/QA ONLY.
-- Seeds the three PC-sheet runtime QA fixtures into the hosted PostgreSQL spine.
-- Run from repository root so the client-side \copy paths resolve.
--
-- Required:
--   psql "$DATABASE_URL" -v allow_qa_seed=true -v actor_user_id='<existing app_user UUID>' \
--     -f database/qa/seed_pc_sheet_runtime_characters.sql

\if :{?allow_qa_seed}
\else
  \echo 'ERROR: pass -v allow_qa_seed=true explicitly.'
  \quit 3
\endif

\if :allow_qa_seed
\else
  \echo 'ERROR: allow_qa_seed must be true.'
  \quit 3
\endif

\if :{?actor_user_id}
\else
  \echo 'ERROR: pass -v actor_user_id=<existing DEV app_user UUID>.'
  \quit 3
\endif

\set qa_campaign_id '7a000000-0000-4000-8000-000000000001'
\set qa_campaign_name 'QA - PC Sheet PDF Runtime'

SELECT CASE WHEN EXISTS (
  SELECT 1 FROM app_user WHERE id = :'actor_user_id'::uuid
) THEN 'true' ELSE 'false' END AS qa_actor_exists
\gset

\if :qa_actor_exists
\else
  \echo 'ERROR: actor_user_id does not exist in app_user; no changes made.'
  \quit 3
\endif

BEGIN;

INSERT INTO campaign(id, name, revision, deleted_at)
VALUES (:'qa_campaign_id'::uuid, :'qa_campaign_name', 0, NULL)
ON CONFLICT (id) DO UPDATE SET
  name = EXCLUDED.name,
  deleted_at = NULL,
  updated_at = now();

INSERT INTO campaign_membership(campaign_id, user_id, role, status)
VALUES (:'qa_campaign_id'::uuid, :'actor_user_id'::uuid, 'DM', 'ACTIVE')
ON CONFLICT (campaign_id, user_id) DO UPDATE SET
  role = 'DM',
  status = 'ACTIVE',
  updated_at = now();

CREATE TEMP TABLE qa_pc_fixture_payload(payload text) ON COMMIT DROP;

-- Aldren Vale
TRUNCATE qa_pc_fixture_payload;
\copy qa_pc_fixture_payload(payload) FROM 'qa/pc-sheet/fixtures/01_aldren_vale_srd5_1_champion_fighter.json' WITH (FORMAT text)

INSERT INTO pc(
  id, campaign_id, owner_user_id, controller_user_id, name, revision, deleted_at,
  snapshot_format, snapshot_version, snapshot, reconciled_at
)
SELECT
  '7a000000-0000-4000-8000-000000000101'::uuid,
  :'qa_campaign_id'::uuid,
  NULL,
  NULL,
  payload::jsonb #>> '{character,name}',
  0,
  NULL,
  'dnd-custom-aid.character-backup',
  2,
  payload::jsonb,
  now()
FROM qa_pc_fixture_payload
ON CONFLICT (id) DO UPDATE SET
  name = EXCLUDED.name,
  revision = CASE
    WHEN pc.snapshot IS DISTINCT FROM EXCLUDED.snapshot
      OR pc.snapshot_format IS DISTINCT FROM EXCLUDED.snapshot_format
      OR pc.snapshot_version IS DISTINCT FROM EXCLUDED.snapshot_version
    THEN pc.revision + 1
    ELSE pc.revision
  END,
  deleted_at = NULL,
  snapshot_format = EXCLUDED.snapshot_format,
  snapshot_version = EXCLUDED.snapshot_version,
  snapshot = EXCLUDED.snapshot,
  reconciled_at = CASE WHEN pc.snapshot IS DISTINCT FROM EXCLUDED.snapshot THEN now() ELSE pc.reconciled_at END,
  updated_at = CASE WHEN pc.snapshot IS DISTINCT FROM EXCLUDED.snapshot THEN now() ELSE pc.updated_at END
WHERE pc.campaign_id = EXCLUDED.campaign_id;

-- Ilyra Quill
TRUNCATE qa_pc_fixture_payload;
\copy qa_pc_fixture_payload(payload) FROM 'qa/pc-sheet/fixtures/02_ilyra_quill_srd5_2_1_evoker_wizard.json' WITH (FORMAT text)

INSERT INTO pc(
  id, campaign_id, owner_user_id, controller_user_id, name, revision, deleted_at,
  snapshot_format, snapshot_version, snapshot, reconciled_at
)
SELECT
  '7a000000-0000-4000-8000-000000000102'::uuid,
  :'qa_campaign_id'::uuid,
  NULL,
  NULL,
  payload::jsonb #>> '{character,name}',
  0,
  NULL,
  'dnd-custom-aid.character-backup',
  2,
  payload::jsonb,
  now()
FROM qa_pc_fixture_payload
ON CONFLICT (id) DO UPDATE SET
  name = EXCLUDED.name,
  revision = CASE
    WHEN pc.snapshot IS DISTINCT FROM EXCLUDED.snapshot
      OR pc.snapshot_format IS DISTINCT FROM EXCLUDED.snapshot_format
      OR pc.snapshot_version IS DISTINCT FROM EXCLUDED.snapshot_version
    THEN pc.revision + 1
    ELSE pc.revision
  END,
  deleted_at = NULL,
  snapshot_format = EXCLUDED.snapshot_format,
  snapshot_version = EXCLUDED.snapshot_version,
  snapshot = EXCLUDED.snapshot,
  reconciled_at = CASE WHEN pc.snapshot IS DISTINCT FROM EXCLUDED.snapshot THEN now() ELSE pc.reconciled_at END,
  updated_at = CASE WHEN pc.snapshot IS DISTINCT FROM EXCLUDED.snapshot THEN now() ELSE pc.updated_at END
WHERE pc.campaign_id = EXCLUDED.campaign_id;

-- Mara de los Siete Umbrales
TRUNCATE qa_pc_fixture_payload;
\copy qa_pc_fixture_payload(payload) FROM 'qa/pc-sheet/fixtures/03_mara_siete_umbrales_custom_extended.json' WITH (FORMAT text)

INSERT INTO pc(
  id, campaign_id, owner_user_id, controller_user_id, name, revision, deleted_at,
  snapshot_format, snapshot_version, snapshot, reconciled_at
)
SELECT
  '7a000000-0000-4000-8000-000000000103'::uuid,
  :'qa_campaign_id'::uuid,
  NULL,
  NULL,
  payload::jsonb #>> '{character,name}',
  0,
  NULL,
  'dnd-custom-aid.character-backup',
  2,
  payload::jsonb,
  now()
FROM qa_pc_fixture_payload
ON CONFLICT (id) DO UPDATE SET
  name = EXCLUDED.name,
  revision = CASE
    WHEN pc.snapshot IS DISTINCT FROM EXCLUDED.snapshot
      OR pc.snapshot_format IS DISTINCT FROM EXCLUDED.snapshot_format
      OR pc.snapshot_version IS DISTINCT FROM EXCLUDED.snapshot_version
    THEN pc.revision + 1
    ELSE pc.revision
  END,
  deleted_at = NULL,
  snapshot_format = EXCLUDED.snapshot_format,
  snapshot_version = EXCLUDED.snapshot_version,
  snapshot = EXCLUDED.snapshot,
  reconciled_at = CASE WHEN pc.snapshot IS DISTINCT FROM EXCLUDED.snapshot THEN now() ELSE pc.reconciled_at END,
  updated_at = CASE WHEN pc.snapshot IS DISTINCT FROM EXCLUDED.snapshot THEN now() ELSE pc.updated_at END
WHERE pc.campaign_id = EXCLUDED.campaign_id;

SELECT CASE WHEN count(*) = 3 THEN 'true' ELSE 'false' END AS qa_seed_ok
FROM pc
WHERE campaign_id = :'qa_campaign_id'::uuid
  AND id IN (
    '7a000000-0000-4000-8000-000000000101'::uuid,
    '7a000000-0000-4000-8000-000000000102'::uuid,
    '7a000000-0000-4000-8000-000000000103'::uuid
  )
  AND deleted_at IS NULL
  AND snapshot_format = 'dnd-custom-aid.character-backup'
  AND snapshot_version = 2
  AND snapshot IS NOT NULL
\gset

\if :qa_seed_ok
\else
  \echo 'ERROR: expected three active QA PC snapshots; rolling back.'
  ROLLBACK;
  \quit 3
\endif

COMMIT;

SELECT
  p.id,
  p.name,
  p.revision,
  p.snapshot #>> '{character,classes,0,rulesFamily}' AS rules_family,
  jsonb_array_length(COALESCE(p.snapshot #> '{character,traits}', '[]'::jsonb)) AS traits,
  jsonb_array_length(COALESCE(p.snapshot #> '{character,inventoryItems}', '[]'::jsonb)) AS inventory_items,
  jsonb_array_length(COALESCE(p.snapshot #> '{character,spells}', '[]'::jsonb)) AS spells
FROM pc p
WHERE p.campaign_id = :'qa_campaign_id'::uuid
  AND p.id IN (
    '7a000000-0000-4000-8000-000000000101'::uuid,
    '7a000000-0000-4000-8000-000000000102'::uuid,
    '7a000000-0000-4000-8000-000000000103'::uuid
  )
ORDER BY p.id;
