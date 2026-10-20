# V11 — P4 control físico independiente de niveles y medidores (2026-10-20)

**Autoridad:** rama `docs/pc-sheet-v11-approved-implementation-handoff` / [PR #173](https://github.com/MrSimkin/dnd_custom_aid/pull/173) DRAFT, SIN MERGE. **Base comprobada:** `f7b602c973ad203eeb1a804a3518e5aba39a775a` (rollback seguro C211; C210 último PASS LOCAL). `main`: `0c009f10d5e66880d68fa3732491c6fc3e172f22`.

## Alcance funcional aislado

Sin reactivar C211 ni modificar renderer, se añade `scripts/audit_v11_p4_slot_meters.py`. Lee **PDF auténticos generados por Desktop App** en `desktopApp/build/pc-sheet-proofs/V11-INTERNAL-PROOF-*.pdf` y verifica sobre geometría/objetos físicos, no solo tests Kotlin:

- P4 existe solo cuando se genera una página de conjuros; los PDF sin P4 siguen sin conjuro inventado.
- En cada documento con P4, los niveles 0–9 aparecen una vez cada uno (ignorando rótulos explícitos de continuación).
- Los niveles 1–9 presentan **nueve ESP.** en total y **tres casillas vectoriales 7×7 cada uno en el mismo renglón**: 27 casillas, sin texto de capacidad inventado por el auditor.
- Se respeta tamaño Carta en las páginas P4 y se inventarían niveles épicos detectados sin afirmar que su origen fue probado.
- Guardas de cobertura: mínimo 48 PDF reales de App y dos personajes/casos con P4, reporte JSON por fichero/página y SHA-256 del PDF.

Cinco pruebas negativas/positivas del auditor (`scripts/test_audit_v11_p4_slot_meters.py`) cubren página correcta, nivel ausente, cuadro omitido, ESP. duplicado y caso no lanzador. **Prueba local preliminar:** 5/5 PASS con PyMuPDF 1.26.7. Se añadió el auditor al paso físico de Scaffold y su JSON al ZIP de auditores de Actions.

**Límite honesto:** esta protección es solo P4/G3 físico Desktop. No coteja todas las páginas con los ocho golden, ni fuentes digitales/slots reales, ni Android instalado; no equivale a G4 PASS, G5, autorización QA o aceptación del propietario. No cambió ningún archivo Kotlin ni las cuatro familias legacy.

## Verificación / continuidad

**Estado al publicar el commit:** CI exacto del nuevo HEAD **PENDIENTE**. Verificar `Scaffold checks` en la rama: ejecución `test_audit_v11_p4_slot_meters.py`, auditor en ≥48 PDF reales, `v11-p4-physical-slot-meters.json` y suites Kotlin/Android/Desktop/backend/DB. Si el auditor falla sobre PDF verdadero, conservar páginas y reporte como nueva observación independiente y diagnosticar antes de cualquier corrección; no relajar expectativas ni marcar PASS automático.

**RCR-1:** C211 BLOCKED 3/3 intentos normales, estructural 0/2; no gastado en este control independiente. **Gates:** G0 histórico PARCIAL; G1 PASS semántico previo; G2/G3 PARCIALES; G4 BLOCKED **105/105 NOT_VERIFIED, active_candidate=null**; G5/G6 NO INICIADOS; APK owner sin PASS. PR #173 no fusionada.

**Siguiente paso seguro:** verificar CI del commit exacto y revisar la evidencia física P4; luego seguir conciliación independiente de otros módulos V11/golden y D-0076 y resolver solo defectos observados de un PDF real, sin reabrir C211 sin su PDF exacto y RCR-1.

## Evidencia física externa adicional del último C210

Se descargó el artefacto **original de PDF Desktop App C210** [#12234489577](https://github.com/MrSimkin/dnd_custom_aid/actions/runs/39355668980/artifacts/12234489577), ZIP SHA-256 `0a4696bdeec0e1cbd53052008c52de552aaf71a5a49cbe829b65fbbc3fceaf48`, y se ejecutó el auditor nuevo sobre **76 PDF reales**: **45/45** PDF con P4 PASS para este control, **49 páginas P4**, 9 ESP. por lanzador, **27 casillas físicas por lanzador (1.215 casillas verificadas)**, 0 errores. Las cinco pruebas locales negativas/positivas también PASS; el ZIP golden conservado mantiene SHA-256 `35a1c6ea...`. Acta reproducible: [evidencia P4 C210](../pc-sheet-v11/qa/evidence/2026-10-20_P4_C210_REAL_DESKTOP_PHYSICAL_AUDIT.json). La evidencia verifica únicamente la **base C210**, no atribuye resultado adelantado al nuevo commit ni cierra la paridad golden o G4. El resultado del **Scaffold del commit `80dd055240f2ed30daadceadb9a7a6adf1c6ff97`** debe comprobarse separadamente.
