# Latest project checkpoint — global resume map

**Updated:** 2026-09-15 (Chile local time)  
**Normal implementation trunk:** `main`  
**Hosted DEV provider activation:** **COMPLETE / VERIFIED**  
**Android hosted-session integration:** **COMPLETE / OWNER-PHYSICAL PASS**  
**Android hosted campaign bootstrap:** **COMPLETE / OWNER-PHYSICAL PASS**  
**Android hosted campaign + PC sync:** **COMPLETE / OWNER-PHYSICAL PASS**  
**Unchanged-sync/no-op confirmation:** **OWNER-PHYSICAL PASS**  
**Multi-client PC convergence safety:** **IMPLEMENTED / AUTOMATED VERIFIED / OWNER-PHYSICAL QA NEXT**  
**Convergence implementation PR:** #39  
**Verified convergence head:** `c1b3b4d55e9e0ee8b66825c7a248c3198dab74fa`  
**Convergence Scaffold gates:** `35034261353` / #2012 and `35034497843` / #2014 — **SUCCESS**  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md` — mandatory project operating rules;
2. `docs/checkpoints/2026-09-15_MULTI_CLIENT_PC_CONVERGENCE_SAFETY_READY_FOR_PHYSICAL_QA.md` — current Wave 4 implementation checkpoint and exact next owner gate;
3. `docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_PC_SYNC_COMPLETE.md` — completed campaign/PC delivery physical evidence;
4. `docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_BOOTSTRAP_COMPLETE.md` — completed ordinary-Player hosted bootstrap proof;
5. `docs/checkpoints/2026-09-15_ANDROID_HOSTED_SESSION_INTEGRATION_COMPLETE.md` — completed Android hosted-session edge;
6. `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md` — hosted DEV provider/environment evidence;
7. `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md` — controlling `$0`, public-repository and owner-guidance policy;
8. `docs/PROJECT_STATE.md` — broader product/engineering state; newer specific checkpoints control where older resume prose is stale;
9. `docs/BRANCH_STATUS.md` — branch lifecycle/resume rule;
10. `docs/ROADMAP.md`, `docs/ARCHITECTURE.md`, `docs/TESTING.md` and `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md` as needed.

If older operational prose conflicts with this file or the current specific checkpoint, the newer specific checkpoint controls unless an even later approved decision/checkpoint supersedes it.

## Important supersession/corrections

- The repository is intentionally **public** under D-0075.
- External-service operating budget remains **USD $0** unless the owner explicitly changes it.
- Neon + Descope + Cloudflare DEV activation is complete; do not restart provider activation.
- Android remembered Descope session and `HostedAccessTokenProvider` are complete; do not create a second authentication/network abstraction.
- Ordinary Player hosted account/campaign bootstrap is complete.
- Campaign local-first creation + durable hosted delivery and PC push/pull are complete through PR #34.
- The real PC wire-envelope `VALIDATION_FAILED` defect was repaired by emitting default-valued backup envelope fields; the preserved blocked mutation was recovered and acknowledged.
- The owner then performed an additional unchanged Player sync and confirmed the outbox remained empty, so the previous no-op physical gate is **PASS**.
- The real Neon database name is `dnd-custom-aid-dev` with hyphens.
- Secret hygiene remains strict regardless of repository visibility.

## Current Wave 4 state

```text
remembered Android Descope session/token           COMPLETE
existing HostedAccessTokenProvider                 COMPLETE
owner-facing hosted account/campaign bootstrap     COMPLETE
campaign create + durable hosted delivery          COMPLETE / OWNER-PHYSICAL PASS
PC snapshot push/pull + blocked-row recovery       COMPLETE / OWNER-PHYSICAL PASS
unchanged-sync no-op confirmation                   COMPLETE / OWNER-PHYSICAL PASS
        |
        v
multi-client PC convergence safety                  IMPLEMENTED / AUTOMATED VERIFIED
        |
        v
ONE consolidated owner physical convergence gate   NEXT
        |
        v
membership revoke + Player/DM authorization        FOLLOWING SEPARATE BOUNDARY
```

## Multi-client convergence implementation

The key correctness requirement is implemented:

> A server-newer PC revision must not silently overwrite an unsent local edit on another client.

The client now persists a normalized last-synchronized PC baseline alongside existing hosted revision metadata. Reconciliation can therefore distinguish:

- local unchanged since last sync + hosted advanced -> safely apply hosted state;
- local changed since baseline + hosted advanced -> preserve local state and report explicit conflict;
- offline local edit + hosted unchanged -> preserve local edit and deliver normally through the durable outbox;
- fresh/second client -> pull the same stable campaign/PC identity and establish a baseline.

Revision + baseline + outbox acknowledgement advance atomically after successful PC delivery. Tombstones, stable IDs, idempotency, non-resurrection, local recovery, DM authority vs PC ownership and owner vs controller semantics remain intact.

For upgraded clients that have old revision metadata but no baseline yet, the client refuses a destructive guess if the server is already newer. An equal-revision sync safely establishes the missing baseline without overwriting a differing local aggregate.

Automated evidence includes focused baseline/migration/conflict tests plus a dedicated two-client sequence covering fresh-client observation, offline delivery, clean convergence and concurrent conflict. Scaffold `35034261353` / #2012 and exact implementation head `c1b3b4d55e9e0ee8b66825c7a248c3198dab74fa` run `35034497843` / #2014 both passed the aggregate gates.

See `docs/checkpoints/2026-09-15_MULTI_CLIENT_PC_CONVERGENCE_SAFETY_READY_FOR_PHYSICAL_QA.md` for full evidence.

## Exact next owner gate

Do **not** split this into repeated install/test loops. The next physical test should cover together:

1. the same hosted campaign/PC observed from a second client state;
2. offline local edit -> reconnect -> successful delivery when the server did not change;
3. server-newer + clean local -> safe convergence;
4. concurrent local + remote edits -> explicit conflict with the local edit preserved.

On an existing upgraded device, perform one clean equal-revision synchronization before intentionally creating the concurrent-edit case so the new baseline can be established if it did not exist previously.

Membership revoke and Player/DM authorization enforcement are the following separate boundary and must not be bundled into this owner gate.

## Hosted DEV status

```text
Neon PostgreSQL             COMPLETE / VERIFIED
Descope identity            COMPLETE / VERIFIED
Cloudflare Worker           COMPLETE / VERIFIED
Real auth round trip        VERIFIED
Real Neon persistence       VERIFIED
Workers Free CPU gate       PASS for tested representative path
Android session edge        COMPLETE / OWNER-PHYSICAL PASS
Android campaign bootstrap  COMPLETE / OWNER-PHYSICAL PASS
Campaign hosted delivery    COMPLETE / OWNER-PHYSICAL PASS
PC snapshot push/pull       COMPLETE / OWNER-PHYSICAL PASS
No-op repeat sync           AUTOMATED VERIFIED / OWNER-PHYSICAL PASS
Multi-client convergence    AUTOMATED VERIFIED / OWNER PHYSICAL QA NEXT
```

Future materially heavier Worker routes should still receive representative CPU/runtime profiling.

## Security residuals carried forward

Important follow-up topics remain:

- JWT/fail-closed verification review;
- object-level authorization regression coverage;
- SQL/query safety;
- error/log secret leakage;
- replay/idempotency authorization;
- exact investigation of the locally reported **3 high severity npm vulnerabilities** — do not run `npm audit fix --force` blindly;
- evaluate a dedicated least-privilege Neon runtime role;
- production-region/identity configuration review before release.

These are visible residuals, not a reason to reopen completed provider/session/bootstrap/campaign/PC packages.

## Historical Player evidence remains bounded

Current integrated CI and hosted physical proofs do not retroactively establish physical owner acceptance of the historical frozen Player candidate. Preserve historical QA evidence for exactly what it tested.
