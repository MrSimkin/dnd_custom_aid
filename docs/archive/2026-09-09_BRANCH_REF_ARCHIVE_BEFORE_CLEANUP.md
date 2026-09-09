# Branch-ref archive before 2026-09-09 night-close cleanup

This file records the final visible branch name → SHA mapping immediately before the owner-approved cleanup after Phase 4A Increment E.

Deleting a branch ref does not rewrite canonical history. This archive exists so an old deleted branch name can still be mapped back to its final commit SHA for historical investigation.

- refs recorded: **60**
- refs scheduled for deletion: **45**
- frozen tmp refs explicitly preserved: **2**
- canonical/current refs preserved: `main`, `implementation/phase4a-successor-cycle`

## Protected frozen refs

- `tmp/phase4-l-frozen-qa-candidate` — KEEP / FROZEN IMMUTABLE
- `tmp/phase4-m5-frozen-qa-candidate` — KEEP / FROZEN IMMUTABLE

## Complete pre-cleanup mapping

| Branch ref | Final SHA before cleanup | Disposition |
|---|---|---|
| `architecture/approved-backend-and-android` | `2d0fbf769fb754d199d3191cc388048f5ebf0070` | KEEP / HISTORICAL MILESTONE LABEL |
| `architecture/phase2-topology` | `a11e347213d35ff2143822b9fb48415974a8d7b3` | KEEP / HISTORICAL MILESTONE LABEL |
| `discovery/initial-product-picture` | `1d246f15500f6fdd61ad62de3d1125ca924a839e` | KEEP / HISTORICAL MILESTONE LABEL |
| `foundation/continuity-structure` | `d9aa474c67ee920fef3fcdd112d9f03b9c065b5f` | KEEP / HISTORICAL MILESTONE LABEL |
| `implementation/character-data-foundation` | `69c9ff1cecc3c0417aa9d378a50c747486447d7c` | KEEP / HISTORICAL MILESTONE LABEL |
| `implementation/initial-scaffold` | `2f8746de1053bf97cc18d7a522f2027e91879251` | KEEP / HISTORICAL MILESTONE LABEL |
| `implementation/local-campaign-selection` | `124626aa6f0fabd449ee5823c1651e3cc01f3e70` | KEEP / HISTORICAL MILESTONE LABEL |
| `implementation/phase4-character-closure` | `c486df837411107d900331649caf89f1cb642984` | KEEP / HISTORICAL MILESTONE LABEL |
| `implementation/phase4-preqa-consolidation` | `4322781ed96dc62af90d4c9966b14b66d4f1ffc0` | KEEP / HISTORICAL MILESTONE LABEL |
| `implementation/phase4-preqa-ux-repair` | `c733e71487b4f4a51c78edb2930c271aafda5a43` | KEEP / HISTORICAL MILESTONE LABEL |
| `implementation/phase4a-successor-cycle` | `70a87b0edc8c583dc0e0f7e1c37d4df4dcfc15ec` | KEEP / CURRENT AUTHORITY |
| `implementation/phase4a-successor-cycle-temp-invalid` | `58e6a8b01397cc7156b0889269b1d07b271a0c34` | DELETE REF / INVALID-SUPERSEDED / ARCHIVED HERE |
| `main` | `698d40b7da75bb7535d83f834db7044ef3e626a8` | KEEP / CURRENT AUTHORITY |
| `origin` | `698d40b7da75bb7535d83f834db7044ef3e626a8` | KEEP / HISTORICAL MILESTONE LABEL |
| `tmp/gate-c-association-cleanup` | `8f932572905a7eaa666daf9fe89ebac59be6e0e7` | DELETE REF / ARCHIVED HERE |
| `tmp/gate-j-final-exact` | `7f93fc5268e9c0a9c26a2642fdbc2348b5f08501` | DELETE REF / ARCHIVED HERE |
| `tmp/gate-j1-exact-retry` | `5a734feb66f1341bed4688a9a8122247f245f8cb` | DELETE REF / ARCHIVED HERE |
| `tmp/gate-j2-exact-retry` | `ac3d596cd0bf6a443f753541b36e24b88cb93b0a` | DELETE REF / ARCHIVED HERE |
| `tmp/gate-k-exact` | `5030a0ed03df4ae92e6de312b1951b7f364c40d7` | DELETE REF / ARCHIVED HERE |
| `tmp/general-b1-safe-edit` | `f0595a9fdc5aeebe8a6f8eef84ca2564e5789f5c` | DELETE REF / ARCHIVED HERE |
| `tmp/increment-d1-navigation-wiring` | `49589a573667c14582929077c420eee5dd09e01b` | DELETE REF / ARCHIVED HERE |
| `tmp/increment-d2-pc-settings-wiring` | `81da860936b0c1f67efbd70c1ec5fa1fc0619de3` | DELETE REF / ARCHIVED HERE |
| `tmp/increment-e-trasfondo-wiring` | `725542622ed31355b475b1471c45798d81036ed7` | DELETE REF / ARCHIVED HERE |
| `tmp/increment-f-rasgos-wiring` | `eb2f53df922b71d813f1b8eb27616854c476ad29` | DELETE REF / ARCHIVED HERE |
| `tmp/increment-g-source-ui-fix` | `d8e0abac9ef6746a2d44b1c4ff01018fe8bdf05d` | DELETE REF / ARCHIVED HERE |
| `tmp/increment-g-source-wiring` | `f1bdbb1caad634160494d86f18b57d8e631ee85a` | DELETE REF / ARCHIVED HERE |
| `tmp/increment-h-spell-list` | `bf8d5d4af8f750edd061d3589af1b6339fd8e8ee` | DELETE REF / ARCHIVED HERE |
| `tmp/increment-i-shared-slot-integration` | `4dd1e86b2ad62cea0789baede6bf20af8bae2b15` | DELETE REF / ARCHIVED HERE |
| `tmp/increment-j-notes-tab` | `2fe7ab0bc6ce18f3956bda0ed750f433367e483b` | DELETE REF / ARCHIVED HERE |
| `tmp/increment-k-responsive-accessibility` | `a43526a1a0ae9d30a0b53023fa4a8b9ee1836f02` | DELETE REF / ARCHIVED HERE |
| `tmp/increment-l-final-regression-qa-target` | `102d4e045462da37538037c13f191d3012041ddd` | DELETE REF / ARCHIVED HERE |
| `tmp/noop` | `58e6a8b01397cc7156b0889269b1d07b271a0c34` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-h3-companions-ui` | `4590ec0e584b8b72fe7b4ce82eb01a00d44de2c8` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-i1-adaptive-shell` | `ebceb1c747ff5649d8b0038ddf38b94b9caafcc6` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-i1-adaptive-shell-wire` | `0dd38bec82885f2a34c203e1c3b23e6be24f8703` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-i1-adaptive-shell-wire-final` | `0dd38bec82885f2a34c203e1c3b23e6be24f8703` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-i1-adaptive-shell-wire-final2` | `0dd38bec82885f2a34c203e1c3b23e6be24f8703` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-i1-adaptive-shell-wire-final3` | `0dd38bec82885f2a34c203e1c3b23e6be24f8703` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-i1-adaptive-shell-wire-final4` | `cf7c2b57879434bdadf836f23d45b1ab19480c3c` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-i1-adaptive-shell-wire2` | `0dd38bec82885f2a34c203e1c3b23e6be24f8703` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-i1-adaptive-shell-wire3` | `0dd38bec82885f2a34c203e1c3b23e6be24f8703` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-i2a-supercompact` | `3d24115bb1fff8aff223a80f4df63a21545539da` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-i2b-table-mode` | `a36a9b36f56b40088c9cb42b55b347a5ecf4c05b` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-j-backup-import` | `a51de04a015c2588de63de189550976ad2b2eeab` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-k-stabilization` | `5cc034d3fdf4c25d935bd698aeaf2a3f9e427f27` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-l-frozen-qa-candidate` | `5cc034d3fdf4c25d935bd698aeaf2a3f9e427f27` | KEEP / FROZEN IMMUTABLE |
| `tmp/phase4-m-audit-safety` | `a82a82478891220b6cee316aa60cddef93888c9b` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-m4-implementation` | `a1becf57a363563b04e285f5580e17b1abc60643` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-m4-scope-holes` | `0af54ea30622812cf4c2837d3e40779bf5ac6e0d` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-m4b-resource-favorites` | `f496e2ed405414587fc4b195f84d57e323311ae0` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-m4c-character-list` | `15774e933ef151975a6fc6659e07a2c4e4bc374d` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-m4d-settings-preview` | `0aecf638328a685dc94ad6019bbf906f9a21db71` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-m4e-rules-source-badges` | `a8dbac6376fe4567a68c79610bbda6270ceff1f3` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-m4f-state-badges` | `5725262de342fd1d65b3bd6669da1d5bcdeb28b2` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-m5-candidate-validator` | `91e2c24399fcb1b4d23d7f14fc497712eacd27fb` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-m5-consolidation` | `c5e501ac275a4935036dd3c5877256b09e3862e7` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-m5-frozen-qa-candidate` | `adc286b3e1305ed706c2ed04d478a43652f6b365` | KEEP / FROZEN IMMUTABLE |
| `tmp/phase4-m6-qa-pause-docs` | `d46e729b87353e595ad513db0ec60043388edbf7` | DELETE REF / ARCHIVED HERE |
| `tmp/phase4-post-l-state` | `c486df837411107d900331649caf89f1cb642984` | DELETE REF / ARCHIVED HERE |
| `tmp/skills-b3-safe-edit` | `90d22e7b2b1cc004b6cab33547fa0a0e9d79668c` | DELETE REF / ARCHIVED HERE |

## Cleanup rule used

The cleanup removes all non-frozen `tmp/*` refs and `implementation/phase4a-successor-cycle-temp-invalid`. Historical milestone branches remain visible. The two frozen QA branches remain immutable.

The night-close consolidation then keeps `main` and `implementation/phase4a-successor-cycle` aligned at the completed-E development baseline. Repository ordering does not imply owner visual acceptance or release readiness.
