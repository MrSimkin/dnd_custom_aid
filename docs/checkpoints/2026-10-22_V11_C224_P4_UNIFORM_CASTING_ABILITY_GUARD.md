# C224 — Aptitud mágica P4 por fuente, no universal falsa

Fecha 2026-10-22 (Chile). Rama `docs/pc-sheet-v11-approved-implementation-handoff`, PR #173 DRAFT / **NO MERGE**. **IN_PROGRESS: CI y auditoría física de PDFs C224 pendientes.**

## Causa y alcance

D-0076 §§3.24–3.26 y contrato visual V11 aprobado: la franja P4 debe reflejar los orígenes mágicos auténticos sin atribuir estadísticas de una fuente a todas las otras. C221 ya corrige perfiles heterogéneos; C223 protege CD y ataque cuando los perfiles son uniformes pero contradicen la ficha global. Brecha C224 independiente: fuentes uniformes configuradas en SAB frente a la aptitud global INT imprimían incorrectamente INT como si representara a esas fuentes. No reabrir C211 (3/3 normales agotados), no rediseñar tarjetas, no tocar familias legacy.

## Cambio funcional y regresiones

- Shared `PcSheetV11SpellSourceLabels.globalAbilityDisagreement`: compara la aptitud explícita de fuentes UUID con uno de seis atributos globales conocidos. Omite divergencias no demostrables ante fuente sin configurar o global `OTHER/NONE`; una fuente con atributo custom frente a global estándar contradice correctamente la atribución universal.
- Android y Desktop V11 usan el mismo método y el rótulo conservador `SEGÚN FUENTE` **solo** en tarjeta `APTITUD` si hay contradicción demostrada. Se mantienen geometría, CD, ataque, identidades UUID, preparación y resto de páginas.
- Shared test: uniformidad entre fuentes y contradicción global, coincidencia, estado no configurado, global OTHER/NONE, aptitud custom. Prueba Desktop desde backup auténtico Ilyra: cambia únicamente aptitud de perfiles persistidos a SAB para ambas variantes A/B, manteniendo hoja global INT, conjuros, CD, ataque, nombres/estados, y exige la tarjeta no engañosa en **PDF real del servicio de la App**; genera dos PDFs A/B para cotejo independiente.

## Procedencia y pendientes

HEAD previo rama: `d73f79d87141d086037b93c9c3d9457bb48e654f`; `main` previo: `0c009f10d5e66880d68fa3732491c6fc3e172f22`. Última evidencia anterior C223: commit `440b74f`, Scaffold #39713904751 + QA #39713904856 SUCCESS, 84 PDF auténticos/777 páginas según acta C223. **Ese PASS LOCAL no se transfiere automáticamente a C224.**

Antes de declarar PASS LOCAL C224: verificar SHA exacto, Scaffold Kotlin Shared/Desktop/Android, QA y auditor PDF; recuperar lote C224 y comparar páginas, texto/posición, vectores e imágenes con 84 PDF C223, inspeccionar los dos A/B nuevos con hashes y rótulo correcto. Si no están los bytes, permanecer `EVIDENCE_PENDING` sin falso PASS; fallos verificados consumen presupuesto propio de C224 conforme RCR-1.

**Gates no promocionados:** G0 QA histórico PARCIAL; G1 semántico PASS anterior; G2/G3 PARCIALES; G4 BLOCKED **105/105 NOT_VERIFIED** y `active_candidate=null`; G5/G6 PENDIENTES. C211 BLOCKED 3/3 normales, 0/2 recuperación estructural. 32 observaciones M50800 y dos V11 QA siguen vigentes; original PDF runtime propietario Mara no recuperado, Android instalado no revisado, QA propietario no aprobado. PR #173 sigue DRAFT sin fusionar.

**Próximo paso:** CI exacto de C224 y auditoría física real antes de más cambios; luego conciliación modular G4 con golden y fuentes D-0076 sin reabrir C211 sin causa y alternativa nuevas.
