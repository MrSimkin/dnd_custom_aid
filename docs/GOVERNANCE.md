# Decision Review and Correction Governance

**Status:** Approved project operating rule  
**Date:** 2026-08-30  
**Decision owner:** Project owner

## 1. Approval is authoritative, but not infallible or irreversible

An owner-approved decision represents the project's best accepted understanding at the time it was made. Approval makes the decision authoritative for current work, but it does **not** make the decision permanently immune from review.

The owner may later discover that:

- an implication was not understood clearly;
- a technical explanation was insufficient;
- a new concrete use case exposes an unintended restriction;
- two approved decisions conflict;
- implementation evidence shows that an earlier assumption was poor;
- a future requirement makes an older decision undesirable.

When that happens, the project must prefer a deliberate correction over blind loyalty to the older approval, even when the correction requires migration, refactoring, compatibility work, or other non-trivial effort.

## 2. Mandatory contradiction detection

Every agent, ChatGPT conversation, coding agent, and human contributor must actively compare significant new requirements, clarifications, and implementation discoveries against existing approved decisions.

If a new statement or use case appears inconsistent with an approved decision, the agent must **surface the contradiction** rather than silently choosing one side or treating the older approval as unquestionable.

Typical triggers include:

- the owner says a previous decision was confusing or may have been misunderstood;
- the owner describes expected behavior that an approved model would prevent;
- implementation reveals a consequence that was not previously explained;
- a new requirement would require violating an earlier approved constraint;
- two repository documents encode incompatible approved behavior.

## 3. Required review procedure

When a possible contradiction or misunderstood approval is detected, the agent should:

1. identify the earlier decision or rule involved;
2. explain the conflict in practical, non-specialist terms;
3. state what the earlier decision was trying to protect or achieve;
4. explain what changing it would affect;
5. state how reversible the decision is and the likely correction cost, including migrations or data-conversion risk where relevant;
6. recommend whether to keep, amend, or supersede the earlier decision;
7. obtain the owner's decision before making a consequential contradictory implementation change;
8. record the resolution and any migration obligation in Git.

Do not use implementation momentum or sunk cost as a reason to conceal a better correction.

## 4. Explanation duty before consequential approval

For significant technical or architectural decisions, the agent must explain enough for an informed owner decision. When practical, include:

- what the decision means in ordinary use;
- a concrete example;
- what the decision enables;
- what it makes harder or prevents;
- whether it can be changed later;
- the likely kind of cost if it is changed later (small refactor, schema/data migration, client compatibility work, major rewrite, etc.);
- important assumptions that could invalidate the recommendation.

The owner is not expected to foresee hidden software-engineering consequences that were not surfaced.

## 5. Approval under uncertainty

The owner may explicitly approve a decision while acknowledging uncertainty or limited foresight. Such a decision is still **Approved** and may guide current work.

That acknowledgement does not weaken the requirement to implement it correctly, but it strengthens the duty of future agents to challenge it if later evidence exposes a contradiction or unintended consequence.

The project should not treat `Approved` as synonymous with `cannot be revisited`.

## 6. Supersession rather than historical erasure

When an approved decision changes:

- preserve the historical record where practical;
- mark the old decision as amended/superseded or otherwise clearly no longer controlling;
- record the new controlling decision and rationale;
- record required migrations/refactors;
- update current-state/product/architecture documents so fresh agents do not follow stale guidance.

The goal is a trustworthy decision history, not pretending earlier decisions were never made.

## 7. Responsibility principle

The owner retains final product authority. Agents retain responsibility for clearly explaining material technical consequences and proactively identifying contradictions, risks, and hidden coupling.

A later correction is a normal part of iterative product development, not a process failure by itself.
