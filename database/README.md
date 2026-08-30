# Hosted database area

This directory is the explicit SQL home for the hosted Neon PostgreSQL schema and migrations.

The scaffold deliberately does **not** invent campaign/character/combat tables before their implementation slices are designed. Add migrations incrementally as approved features require them.

See `migrations/README.md` for migration-file expectations.
