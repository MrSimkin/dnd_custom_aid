# Revisión automática del contenido del SRD

Punto 11 de la revisión crítica: «los datos extraídos automáticamente pueden traer errores sin aviso». Desde el 18-11-2026 hay una revisión que se ejecuta con el proyecto y dice qué quedó incompleto.

```powershell
python scripts/check_srd_content.py
```

## Qué revisa

- **Monstruos:** que cada uno tenga CA, PV, algo que hacer (acciones, acciones legendarias o rasgos) y valor de desafío.
- **Clases:** que lleguen a nivel 20, con dado de golpe, salvaciones y habilidades para elegir.
- **Subclases, razas, trasfondos y dotes:** que no estén vacíos.

## Cómo evita que empeore

Las cuentas de cada problema quedan en `SRD_REVISION_BASELINE.json`. Si una construcción deja más casos incompletos que esa referencia, la revisión falla y nombra los culpables. Cuando un cambio los arregla —o cuando se acepta uno nuevo a sabiendas— se actualiza la referencia:

```powershell
python scripts/check_srd_content.py --update-baseline
```

## Lo que encontró la primera pasada (18-11-2026)

- **Reparado: el Guerrero de 5e llegaba solo a nivel 11.** Su tabla de niveles se parte en dos por un salto de página del PDF, y la segunda mitad llegaba como párrafo. `scripts/build_srd_rules.py` ahora también lee esa continuación, así que los niveles 12 a 20 volvieron (Mejora de Característica, Indómito de dos y tres usos, Acción Súbita de dos usos y Ataque Adicional (3)). Afectaba a cualquier guerrero de 5e de nivel 12 o más creado con el asistente.
- **Quedan cinco avisos conocidos, que son del SRD o de su maquetación:**
  - «Avatar de la muerte» (5e y 5.5e) no tiene PV porque el propio SRD dice «la mitad del máximo de su invocador»;
  - «Caballito de mar» (5e) y «Hongo chillón» (5.5e) quedaron sin nada que hacer: su único rasgo se perdió al separar las fichas;
  - «Mosca gigante» (5e) quedó sin valor de desafío porque su ficha se corta contra la entrada siguiente del documento.

  Estos cinco están en la referencia. Se revisan cuando se retome la extracción de monstruos; mientras tanto, la revisión avisa si aparecen más.
