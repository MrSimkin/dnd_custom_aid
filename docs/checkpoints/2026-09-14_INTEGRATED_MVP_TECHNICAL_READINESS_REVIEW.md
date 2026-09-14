# 2026-09-14 — Integrated MVP technical readiness review

**Status:** COMPLETE — technical planning/readiness review only  
**Scope:** repository/runtime convergence, hosted/client architecture details, testing/readiness risks  
**Product-code authorization:** NOT granted by this review  
**Starting `main` HEAD:** `c68953aeedc5e270261536610887ef6ad42cf6eb`  
**Player successor HEAD observed:** `b9dea8ad6b17dcf3feeabba263eff1ee498f1536`  
**Frozen Player candidate:** `0.4.0-preqa.13 / 41300` at `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`

## Purpose

D-0073 delegates routine low-level engineering choices to the technical assistant/implementation workers rather than requiring the owner to rubber-stamp matters outside their intended competence. The owner therefore requested a technical review before the next genuine owner intervention.

This checkpoint records that review so a future Worker can continue from Git rather than from chat memory.

No Player, backend, database, Desktop or application implementation is changed by this checkpoint.

---

## 1. Repository and Player evidence verified

`main` was verified at `c68953aeedc5e270261536610887ef6ad42cf6eb` before this review.

The authoritative Player line remains:

`implementation/phase4a-successor-cycle`

Observed branch HEAD:

`b9dea8ad6b17dcf3feeabba263eff1ee498f1536`

That HEAD is documentation after the frozen candidate rather than a different APK candidate.

The branch's current checkpoint identifies:

- version `0.4.0-preqa.13`;
- versionCode `41300`;
- candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold run `34801612526` / #1630;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted cross-device physical revalidation pending;
- Phase 4A formally open on that historical Player line.

GitHub Actions run `34801612526` was independently checked during this review and is `completed / success` against the exact candidate SHA.

Green automation remains technical evidence, not retroactive physical owner acceptance.

---

## 2. Branch divergence and convergence risk

Comparing current `main` with `implementation/phase4a-successor-cycle` showed:

- status: `diverged`;
- Player successor ahead by 574 commits;
- Player successor behind `main` by 40 commits;
- merge base: `f7f0389fde4fd3a72ca8f8a547dae38255825266`.

The raw commit count looks larger than the practical integration risk. The successor contains the large Player/runtime evolution, while later `main` work is predominantly global product/architecture/documentation. `main` has not independently built a competing hosted backend, hosted database or substantial Desktop product.

Therefore D-0073's semantic convergence rule remains technically sound:

- Player runtime, local SQLDelight migrations, Player tests/guards and exact Player evidence come from the successor line;
- later integrated product/architecture/governance documentation comes from current `main`;
- shared files such as CI/governance/navigation must be deliberately reconciled rather than taking either branch wholesale;
- frozen candidate evidence is preserved as historical evidence even though the integrated baseline will later move beyond that candidate.

The convergence should occur on a dedicated branch only after explicit implementation authorization.

---

## 3. Current implementation reality

### 3.1 Backend

`backend/` is genuinely a scaffold. The Worker currently exposes only `/health` and otherwise returns 404. The current package surface is essentially Wrangler + TypeScript type-checking.

This is useful: there is little hosted legacy to unwind.

### 3.2 Hosted PostgreSQL

`database/migrations/` currently contains only its README. There is no competing real hosted application schema yet.

The hosted relational model can therefore be built directly around the approved D-0071/D-0073 contracts.

### 3.3 Desktop

`desktopApp/` is also genuinely a scaffold: the current main window only displays a basic desktop placeholder. The complete D-0072 Desktop product can therefore be built without dismantling an obsolete Desktop architecture.

### 3.4 Shared/Player foundation

The substantial existing asset is the Player/shared implementation, not the backend/Desktop scaffold.

The shared module already provides:

- Kotlin Multiplatform Android + JVM/Desktop targets;
- SQLDelight SQLite persistence;
- kotlinx.serialization;
- stable UUID-based domain identity;
- mature Player repositories/domain operations;
- a long, tested local migration history;
- extensive Player-domain automated tests;
- current Player guard scripts in the successor CI workflow.

The convergence must protect this foundation rather than reimplementing Player state for server convenience.

---

## 4. Important Player technical assets to preserve

### 4.1 Versioned full-PC serialization

The successor already has a versioned application-owned `CharacterBackupDocument` using kotlinx.serialization JSON. It includes the authoritative `CharacterSheet`, `CharacterClosureState` and `CharacterSuccessorState`, validates format/version/payload, tolerates unknown future keys, and remaps identities when imported as a new copy.

This is a strong foundation for hosted PC synchronization.

### 4.2 Recommended hosted PC representation

Do **not** mirror the entire normalized local SQLDelight character graph one-for-one into hosted PostgreSQL merely because those tables exist locally.

Preferred first hosted representation:

- relational/indexed server metadata for authorization, campaign membership, owner/controller, revision, status/tombstone, public identity projection, asset references and timestamps;
- a versioned canonical PC snapshot stored as JSONB/document payload using the same application-owned serialization family as the proven Player backup codec;
- grouped audit/history and selected recovery snapshots stored separately from current state.

This keeps the local offline schema optimized for native editing while avoiding a second enormous server-side character persistence model that must evolve in lockstep with every local table.

The backend must never expose the complete PC snapshot merely because it is convenient; authorization and the tiny Player-to-Player public projection remain server-enforced.

### 4.3 Existing Player provenance

The successor's character trait provenance work is valuable and must be preserved. It deliberately keeps stable gameplay provenance identities and allows deleted source references to become unresolved rather than silently remapping/cascading.

However, this Player trait-provenance model is **not** the same semantic problem as Personal -> Campaign copy provenance for Monsters/NPCs/Homebrew/Encounters. Reuse the principle, not necessarily the exact class/table shape.

### 4.4 Local migrations

The successor line has advanced SQLDelight migrations through at least `13.sqm` and `14.sqm`, including spell-source origin metadata and owned class/species/background/provenance identities.

These migrations and their tests are part of the authoritative Player runtime and must survive convergence intact.

---

## 5. Technical implementation direction after convergence

These are delegated engineering choices under D-0073. They are not additional owner product decisions.

### 5.1 Shared native HTTP client

Use **Ktor Client** in the shared Kotlin layer for common JSON/request/auth/sync code, with appropriate Android/JVM engines configured per target. This fits the existing Kotlin Multiplatform shape and avoids separate handwritten Android/Desktop network stacks.

### 5.2 API style

Use a small versioned HTTP/JSON application API, initially under a stable `/v1` family.

Writes should carry:

- authenticated user identity established server-side;
- stable object identity;
- a client-generated mutation/idempotency identifier;
- the revision the client believed it was editing where concurrency protection applies.

The server assigns/advances authoritative revisions and rejects stale writes rather than silently using last-write-wins.

Use a stable machine-readable error envelope so clients can distinguish authorization, conflict, validation, not-found and transient/server failures without parsing display strings.

### 5.3 Project-specific sync

Implement purpose-built sync rather than a generalized synchronization platform.

Recommended shape:

- local SQLDelight outbox/pending-mutation records;
- stable mutation UUIDs for safe retry;
- push mutations with expected/base revision;
- server-side idempotency handling;
- scoped server change sequence/cursor for pulling changes relevant to the active campaign/library;
- transactional local application of accepted remote batches;
- tombstones for synchronized deletion/non-resurrection;
- explicit conflict state for genuine revision conflicts.

Desktop only transmits pending work on explicit Sync. Android may opportunistically retry while retaining manual Sync. Local Save remains independent from the network.

### 5.4 Hosted PostgreSQL access

Use the **Neon serverless driver** from the Cloudflare Worker initially. It is designed for serverless/V8-isolate environments including Cloudflare Workers and avoids adding another infrastructure layer.

Do not introduce Hyperdrive initially. Revisit it only if measured connection/latency behavior demonstrates a real benefit; do not stack Hyperdrive on top of the Neon serverless driver.

Keep hosted schema evolution as explicit SQL migrations under `database/migrations/`. An ORM is not required merely to create typed abstractions.

### 5.5 Backend structure

Evolve the Worker from its current health-only scaffold into a small layered service with:

- authentication/session validation middleware;
- application/domain authorization;
- request validation;
- routes by domain;
- PostgreSQL access layer;
- sync/idempotency/revision services;
- object-storage service;
- backup/export service.

A lightweight Worker-native router/middleware library may be introduced if it materially reduces boilerplate, but the product does not need a large web framework.

### 5.6 Descope integration

Authentication proof remains Descope; campaign/domain authorization remains application-owned.

Preferred client/backend split:

- Android: Descope's native Kotlin/Android SDK;
- Desktop JVM: standards-based OIDC native flow, using Authorization Code + PKCE/system browser rather than embedding web credentials in the app;
- backend: validate Descope session/access tokens server-side on protected API requests, including audience/configuration checks as appropriate.

A short implementation spike should confirm the cleanest Cloudflare-Worker-compatible validation library/SDK. If the Node SDK is awkward in the Worker runtime, standards-based JWT/JWKS validation is an acceptable fallback; this does not change the product contract.

### 5.7 Object storage recommendation

**Cloudflare R2 Standard is the preferred technical provider** for the first implementation because:

- the backend already runs on Cloudflare Workers;
- Worker bindings give the backend a direct server-side storage path;
- R2 currently includes 10 GB-month storage, 1 million Class A operations and 10 million Class B operations per month in the Standard free tier;
- egress is currently free;
- personal-project scale is likely to remain within or near the included usage for a substantial period.

This remains a **recommendation pending owner/service activation**, not an already-consumed billing commitment. Enabling R2 requires a Cloudflare R2 subscription/checkout relationship even though included usage may make the actual initial charge zero.

Native clients should never receive durable R2 credentials. Asset identity remains application-level; provider object keys stay infrastructure-specific.

The exact first upload strategy (Worker-proxied vs short-lived authorized direct upload/presigned flow) can be selected when concrete asset-size constraints are implemented. Do not build multipart/presigned complexity until actual media size/workflow justifies it.

### 5.8 Import/export format

Use a versioned application-owned JSON document family as the canonical import/export representation, following the successful Player backup pattern:

- explicit format identifier;
- schema/version number;
- content type;
- structured records;
- validation before commit;
- preview of warnings/errors;
- explicit destination scope;
- no overwrite-by-name behavior.

Bulk record arrays/packages may be added for content families that need them. CSV/plain text can later be convenience adapters, not the canonical nested format.

### 5.9 Backup/export

For this personal-scale MVP, prefer an on-demand versioned backup archive rather than queues/event infrastructure.

The export should include at least:

- manifest with application/schema/backup-format versions and creation time;
- durable relational data export;
- asset manifest plus sufficient binary recovery material/reference according to the final object-storage design;
- per-file or manifest checksums/integrity information;
- enough metadata to detect incomplete generation.

A ZIP-like archive with JSON/NDJSON plus assets is a reasonable first implementation. The exact internal layout is an engineering detail and should be versioned.

### 5.10 CI/testing after convergence

The integrated baseline must preserve every permanent Player guard from the successor workflow before adding new hosted checks.

Expand CI proportionately with:

- shared contract serialization tests;
- hosted PostgreSQL migration tests;
- backend route/auth/authorization tests;
- stale-revision/idempotency/tombstone tests;
- sync round-trip tests;
- backup manifest/integrity tests;
- later asset and combat-authority integration tests.

Do not replace the proven Player guard suite with generic coverage metrics.

---

## 6. Planned convergence execution

When the owner explicitly authorizes implementation:

1. refresh `main` and `implementation/phase4a-successor-cycle` again;
2. create a dedicated convergence branch from current `main`;
3. reconcile the successor into it deliberately;
4. resolve Player/runtime files, Player SQLDelight migrations, Player tests and Player guard scripts in favor of the authoritative successor implementation unless a current global decision explicitly changes behavior;
5. resolve current product/architecture/governance docs in favor of current `main`, while updating them to the new integrated-trunk state;
6. reconcile the CI workflow by retaining the successor Player guard steps plus current backend/desktop build gates;
7. run the aggregate Kotlin/Android/Desktop tests/build plus Player guard scripts and backend check;
8. record exact unresolved conflicts, if any;
9. only merge the convergence branch to `main` if the baseline is coherent/buildable and no valid work from either authority line was lost;
10. then freeze the old Player successor line as historical evidence and use `main` as the integrated trunk.

The convergence itself is implementation work and remains blocked until explicit owner authorization.

---

## 7. Readiness risks found

### 7.1 Stale mandatory governance documents — found and being repaired by this documentation pass

Several mandatory entry files still contained `preqa.9`, `finish 7D/7E`, or `DM implementation blocked until Phase 4A owner closure` language. A future Worker following those instructions literally could take the wrong path despite D-0073.

This technical-readiness documentation branch therefore reconciles those entry documents to D-0071/D-0072/D-0073 and the current `preqa.13` evidence.

### 7.2 Authentication runtime compatibility — bounded implementation spike

Confirm the most proportional Descope token-validation implementation inside the Cloudflare Worker before spreading an SDK dependency through the backend. This is a technical spike, not an owner decision unless the outcome would change provider/cost/security assumptions.

### 7.3 External service activation

Actual hosted implementation will require valid project/account configuration and secrets for Cloudflare, Neon and Descope. R2 additionally requires the owner to enable an R2 subscription/checkout relationship if it has not already been enabled.

Secrets remain outside Git.

### 7.4 Asset upload size

Do not pre-build a complex upload system before the real first media/handout limits are known. Start with the simplest safe authorized path and introduce multipart/direct-upload machinery only if actual file-size behavior requires it.

---

## 8. Technical review conclusion

No unresolved low-level technical question currently requires the owner to choose between engineering alternatives.

The architecture is technically coherent enough to begin the planned convergence and first integrated-MVP implementation packages once explicitly authorized.

The current recommended sequence is:

```text
explicit owner implementation authorization
-> dedicated main + Player successor convergence
-> validated integrated baseline on main
-> shared/hosted MVP spine
-> Player <-> hosted end-to-end
-> Desktop/admin/content/live work in dependency waves
-> combat authority/public projection
-> SRD clarification
-> integrated owner QA
```

The next owner intervention is therefore **not** another technical design approval. It is the meaningful product/project authorization:

> authorize beginning the integrated-MVP implementation, starting with the protected branch convergence.

After that authorization, the next likely owner intervention is external account/service setup only when needed — especially enabling R2 and providing/configuring the relevant Cloudflare/Neon/Descope project secrets through secure channels — rather than making schema/API decisions.