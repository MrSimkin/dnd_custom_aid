# Backend scaffold

Minimal Cloudflare Worker/API shell for `dnd_custom_aid`.

Current scope is intentionally tiny:

- `GET /health` returns a JSON health response;
- no Neon binding/connection yet;
- no Descope integration yet;
- no R2, Durable Objects, WebSockets, queues, deployment automation, or other deferred infrastructure.

Commands from this directory:

```bash
npm install
npm run check
npm run dev
```

`npm run check` generates Worker types from `wrangler.jsonc` and then runs TypeScript type checking.
