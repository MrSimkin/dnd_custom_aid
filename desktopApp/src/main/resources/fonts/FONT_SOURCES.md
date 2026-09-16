# Desktop bundled font sources

The Desktop application reuses two font binaries that were already present in this public repository for the Android application:

- `geist_vf.ttf` — Geist, upstream `vercel/geist-font`, SIL Open Font License 1.1. See `LICENSE_Geist_OFL.txt`.
- `mona_sans_condensed_vf.ttf` — Mona Sans Condensed, upstream `github/mona-sans`, SIL Open Font License 1.1. See `LICENSE_MonaSans_OFL.txt`.

They are copied byte-for-byte from the existing Android resources into the Desktop application's runtime resource directory so Compose Desktop can load them without a network font provider.

The remaining names in the current Android-selectable catalogue are not bundled here. Desktop exposes those names only when that exact font family is installed on the local operating system. This preserves honest previews and avoids presenting a named font while silently rendering an unrelated fallback.

No runtime font downloader, paid service, provider credential, or repository secret is required.