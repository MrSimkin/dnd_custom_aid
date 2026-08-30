# D-0041 — SRD clarification uses PostgreSQL full-text retrieval and a replaceable LLM

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner

MVP rules clarification will use the official Spanish **SRD 5.1** and **SRD 5.2.1** corpus stored as versioned, provenance-preserving sections/chunks in PostgreSQL.

## Retrieval

- Initial retrieval uses ordinary PostgreSQL full-text search (`tsvector` / `tsquery` with suitable indexes).
- Retrieved chunks retain exact SRD version and section/source provenance.
- The corpus is small enough that PostgreSQL storage is acceptable within the selected Neon personal-scale architecture; no separate vector database is justified initially.
- Embeddings/vector or hybrid retrieval are explicitly deferred unless real testing demonstrates that full-text retrieval is materially inadequate for the desired Spanish natural-language questions.

## Clarification generation

- Relevant retrieved official-SRD excerpts are supplied to a replaceable LLM integration.
- **Cloudflare Workers AI** is the initial provider because Cloudflare is already part of the selected backend topology and expected personal-scale use fits the available no-cost capacity.
- The exact model is configuration rather than a durable architecture choice so it can be replaced when models change or improve.
- The assistant must ground MVP answers in retrieved supported official-SRD material rather than silently relying on general model knowledge.
- User-facing answers are in Spanish and identify the applicable D&D 5e / SRD 5.1 or D&D 5.5e / SRD 5.2.1 source/version as required by C-0007.
- Campaign house rules/homebrew are outside MVP clarification scope under existing product decisions.

## Proportionality

This is intentionally a small retrieval architecture for two known rule corpora. Do not add an embeddings pipeline, vector database, separate retrieval service, elaborate RAG platform, or additional AI provider unless measured retrieval quality or another concrete requirement justifies it.

This resolves the SRD corpus storage/retrieval/clarification portion of D-0009. D-0009 remains Pending until the remaining consequential architecture choices are approved.

> Safety checkpoint note: this decision is stored as a dedicated decision file on the active architecture branch so it is durable immediately. It should be consolidated into the chronological `docs/DECISIONS.md` log before the architecture branch is merged.
