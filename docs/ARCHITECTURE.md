# Architecture Record

## Current status

**Architecture status:** Not selected  
**Application code:** Not yet scaffolded

No Android framework, programming language, UI toolkit, dependency-injection approach, database, networking stack, sync service, or minimum Android version is currently approved.

## Architecture principles already justified by project needs

These are design constraints, not technology selections:

1. **Phone and tablet are first-class targets.** The architecture must support adaptive layouts without maintaining two unrelated applications.
2. **Reproducibility matters.** A fresh agent should be able to build and test from repository instructions rather than hidden local setup.
3. **Testability matters.** Approved behavior should be verifiable without depending exclusively on manual tapping.
4. **Continuity matters.** Important architecture decisions must be documented with rationale and consequences.
5. **Avoid unnecessary lock-in.** Dependencies/services with meaningful cost, privacy, or migration implications require explicit consideration.
6. **Secrets stay outside source control.** Any future credentials/signing material must use documented secure handling.

## Pending architecture decision

See D-0009 in `docs/DECISIONS.md`.

The architecture decision should be made only after the MVP/product discovery provides enough information to evaluate the real needs.

At minimum, compare options on:

- Android phone/tablet support quality;
- development and maintenance complexity;
- automated-test support;
- offline/local-data requirements;
- accessibility support;
- long-term ecosystem maturity;
- dependency burden;
- AI-assisted development suitability;
- build reproducibility;
- reversibility of the choice.

## Decision-record rule

When architecture is chosen, this file must record:

- chosen stack and versions/baselines;
- alternatives considered;
- plain-language reason for the choice;
- important trade-offs;
- project/module structure;
- data flow;
- persistence strategy;
- navigation strategy;
- testing strategy;
- dependency policy;
- build/run commands;
- migration/reversal considerations.

Significant later architecture changes must be added to `docs/DECISIONS.md` rather than silently replacing the rationale here.
