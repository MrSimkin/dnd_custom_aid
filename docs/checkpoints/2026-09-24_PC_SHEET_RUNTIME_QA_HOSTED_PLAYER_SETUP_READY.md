# Checkpoint - PC Sheet runtime QA hosted Player setup ready

**Date:** 2026-09-24 (Chile local time)  
**Base integrated main before this documentation update:** `0cec4ed915afc6b1f814ff0987d6c6f6078bc9cd`  
**Status:** HOSTED QA DATA READY / GMAIL PLAYER AUTHORITY VERIFIED / MANUAL ANDROID SMOKE STILL PENDING

## Why this exists

The three integrated PC Sheet runtime QA fixtures are now present in the real hosted DEV PostgreSQL database and assigned to a deliberate secondary real Player identity for the pending Android device/runtime PDF smoke.

This closes only the QA-data preparation boundary. It does **not** close the Android Save/Share runtime gate and does not authorize Media / Handouts work yet.

## Verified hosted DEV target

Existing DEV provider state remains unchanged:

- Neon project: `dnd-custom-aid-dev`;
- project ID: `holy-meadow-19010740`;
- branch: `production`;
- database: `dnd-custom-aid-dev`;
- PostgreSQL server observed during this owner session: 17.11.

No new provider resource, paid service or credential was created.

## Real QA identities

For this specific PC Sheet runtime QA:

- Outlook app_user `f34bc5f0-4d35-4d09-b771-505b3851440c` remains `DM / ACTIVE`;
- Gmail app_user `4ba0f476-2eba-4eff-b4e0-78bb9372a8b4` is deliberately reused as `PLAYER / ACTIVE`.

This is a deliberate exception to the normal hosted DEV rule where Gmail remains historical/inactive by default. Outlook remains the canonical DEV owner/DM identity. Gmail is being used here specifically because a Player-owned/controller path gives stricter runtime authorization coverage than DM-wide visibility.

## QA campaign

Dedicated deterministic campaign:

- name: `QA - PC Sheet PDF Runtime`;
- id: `7a000000-0000-4000-8000-000000000001`.

Verified membership state:

- Gmail -> `PLAYER / ACTIVE`;
- Outlook -> `DM / ACTIVE`.

## Hosted QA PCs

The exact integrated Character Backup v2 fixtures were loaded as:

1. Aldren Vale
   - id: `7a000000-0000-4000-8000-000000000101`;
   - owner: Gmail;
   - controller: Gmail;
   - revision: `0`;
   - snapshot format: `dnd-custom-aid.character-backup`;
   - snapshot version: `2`.

2. Ilyra Quill
   - id: `7a000000-0000-4000-8000-000000000102`;
   - owner: Gmail;
   - controller: Gmail;
   - revision: `0`;
   - snapshot format: `dnd-custom-aid.character-backup`;
   - snapshot version: `2`.

3. Mara de los Siete Umbrales
   - id: `7a000000-0000-4000-8000-000000000103`;
   - owner: Gmail;
   - controller: Gmail;
   - revision: `0`;
   - snapshot format: `dnd-custom-aid.character-backup`;
   - snapshot version: `2`.

PC ownership/controller assignment intentionally did not advance PC snapshot revision/content, consistent with the repository PC-authority contract.

## Fixture integrity

Before generating the Neon-compatible SQL, the owner-local fixture files were checked against the current repository Git blobs:

- Aldren: `498a47e919421a67d8bb04346619310811296e15`;
- Ilyra: `9b980847a85542fec423730593e9294714fb552f`;
- Mara: `d33dd0aaf18958014a7507b3226f375029e0fda4`.

The hosted payloads therefore originate from the exact integrated QA fixtures rather than manually reconstructed character data.

## Owner workstation / Neon CLI observation

Known owner clone remains:

`D:\DnD_Aid\repo\dnd_custom_aid`

During this QA setup:

- `neon --version` reported `4.18.0`;
- native `psql` was not on PATH;
- `neon psql` successfully connected through the embedded TypeScript psql client;
- database/role discovery succeeded against `dnd-custom-aid-dev` / `dnd-custom-aid-dev_owner`;
- the observed embedded interactive client rejected `\\i` and did not execute the forwarded `-f` probe in this invocation;
- no repository seed mutation was performed through that failed path;
- the owner instead ran one pure-PostgreSQL SQL Editor adaptation generated from the exact local fixture JSON files.

Do not assume the owner must install native PostgreSQL merely to repeat this QA. If the embedded CLI behavior remains the same, the Neon SQL Editor path is the proven route.

## Verification returned by Neon

Membership verification returned exactly:

- Gmail -> PLAYER / ACTIVE;
- Outlook -> DM / ACTIVE.

PC verification returned exactly the three expected deterministic IDs, each with:

- owner = Gmail;
- controller = Gmail;
- revision = 0;
- snapshot format = `dnd-custom-aid.character-backup`;
- snapshot version = 2.

## Authorization meaning

The backend `listPcSnapshots` contract requires an ACTIVE campaign membership and then permits:

- all PCs for an ACTIVE DM; or
- only PCs owned/controlled by an ACTIVE Player.

Therefore signing Android into the Gmail identity now exercises the intended Player authorization path for all three QA PCs rather than relying on DM-wide access.

## Remaining manual Android gate

The next action is now entirely on the real Android device:

1. sign into the app with the Gmail DEV identity;
2. refresh/synchronize hosted campaigns and PCs;
3. verify `QA - PC Sheet PDF Runtime` appears;
4. verify Aldren, Ilyra and Mara appear through the Player path;
5. Aldren: real document-picker Save, open the PDF, then Share through a real compatible target;
6. Ilyra: Custom v2 + Spellbook, make one harmless unsaved edit, choose `Exportar sin guardar`, and verify the PDF reflects the edit while persisted character data does not;
7. Mara: test both Custom-v2 Attribute/Ability variants with Extended Page and inspect custom-stat/overflow continuation pages;
8. request Current Snapshot once and verify the known fallback notice / Permanent fallback;
9. record PASS or exact defects in the repository.

Do **not** start Media / Handouts before this manual runtime result is recorded.
