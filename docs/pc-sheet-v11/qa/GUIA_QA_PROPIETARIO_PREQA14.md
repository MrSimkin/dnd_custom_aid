# Guía de QA del propietario — V11 `0.5.0-preqa.14`, build 51400

**Fecha:** 2026-11-10. **Para:** el propietario. **Hecha por:** el Worker.

Esta ronda revisa los comentarios del QA de `preqa.13` (V11-QA-007 a V11-QA-018) y las decisiones del propietario del 2026-11-10. Como en `preqa.13`, va **sin** el control final ni una revisión independiente, por decisión del propietario del 2026-11-08.

## 1. Qué recibes

| Archivo | Qué es |
|---|---|
| `D:\DnD_Aid\APKs\dnd-custom-aid-0.5.0-preqa.14-build-51400.apk` | La App. Su SHA-256 está en `ACTA_CANDIDATO_PREQA14.md` |
| Página de QA en claude.ai | Las pruebas con botones Bien / Falla / No aplica; las marcas se guardan solas |

Los tres PJ ya están en la App del propietario, sincronizados desde el servidor; no hace falta importarlos. Aldren conserva su conflicto de sincronización (Cobre 12) hasta que exista la resolución de conflictos (V11-QA-004).

## 2. Instalar

- **Emulador:** arrastra el APK sobre la ventana del emulador.
- **Teléfono:** copia el APK al teléfono y ábrelo desde Archivos.

Se instala encima de `preqa.13`. Si Android pide desinstalar, no lo hagas: avisa.

## 3. Sacar los PDF

En el PJ: **Ajustes → Hoja de personaje PDF**. Elige **A · Habilidades por atributo** o **B · Habilidades en orden alfabético**, marca **Números en tinta clara** o **Incluir Libro de conjuros** si hace falta, y toca **Guardar PDF**.

| PJ | Exportar |
|---|---|
| Aldren | A, A con tinta clara, B |
| Ilyra | A con Libro, B |
| Mara | A, B con Libro |

Para niveles épicos (prueba F11): **Editar → la clase → «Habilitar niveles épicos»**, junto a los dados de golpe.

## 4. Traer los PDF al PC

```powershell
New-Item -ItemType Directory -Force -Path "D:\DnD_Aid\PDF Check" | Out-Null
Set-Location "D:\DnD_Aid\PDF Check"
adb pull "/sdcard/Download/." "."
```

## 5. Qué mirar

| Prueba | Comentario de origen | Qué debe verse |
|---|---|---|
| P2 | V11-QA-007 | Pantalla con A y B de nombre claro, tinta clara y Libro; sin las hojas antiguas |
| F1 | V11-QA-009 | «Guerrero (Champion) 5». El 2026-11-06 el propietario dejó la subclase solo en la App (GLB14); su pedido del 2026-11-10 la imprime. Debe confirmarlo |
| F2 | V11-QA-008 | Retrato sin foto en línea fina |
| F3 | V11-QA-010 | Números variables claros solo con la opción |
| F4 | V11-QA-011 | Equipo Especial: objetos a su altura, filas libres vacías de una línea |
| F5 | V11-QA-012 | Sangría en las líneas que siguen de un rasgo |
| F6 | V11-QA-013 | ACCIONES y RECURSOS de la pág. 4 |
| F7 | V11-QA-015 | Espacios de conjuro por columnas, alineados a la izquierda |
| F8 | V11-QA-016 | Fila del Spellbook sin línea vacía |
| F9 | V11-QA-017 | EQUIPO en continuación con la forma de P2 |
| F10 | Decisión 2026-11-10 | B en orden alfabético |
| F11 | V11-QA-018 | N10 a N12 en una cuarta fila |
| R1–R4 | — | Que nada de lo que estaba bien se haya roto |

## 6. Lo que no está demostrado

- **Control final:** no pasa; el propietario decidió seguir sin él.
- **Velocidad y daño** quedan en tinta normal con la opción de tinta clara, porque llevan texto («30 pies · 9 m», «1d8 + 6 cortante»). Si el propietario los quiere claros, se ajusta.
- **Mara del propietario:** sus 5 objetos especiales ahora caben en P2, así que Equipo Especial no sigue en la continuación. La Mara de prueba del repositorio tiene un sexto objeto para cubrir ese caso.
- **Pendientes que no entran en `preqa.14`:** resolución de conflictos (V11-QA-004), pantalla DEV (V11-QA-005), pantalla de login (V11-QA-006). Su diseño se presenta con imágenes antes de programarlos.
