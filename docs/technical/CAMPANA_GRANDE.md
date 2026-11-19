# Cómo se porta con una campaña grande

Punto «M» de la lista del propietario (18-11-2026): «espero que haya más material». El set Campus Celeste ya trae 1083 objetos, así que la pregunta es qué pasa cuando sean el doble.

## La medición

`LargeCampaignPerformanceTest` arma una campaña de **2000 objetos** (400 lugares, 900 PNJ con su lugar enlazado, 300 escenas y 400 criaturas) y mide lo que el DM espera mirando la pantalla.

Medido el 18-11-2026 en el equipo del propietario:

| Qué | Tiempo |
| --- | --- |
| Construir los 2000 objetos | 711 ms |
| Abrir la lista de PNJ (900) | 6 ms |
| Buscar en toda la campaña | 343 ms |
| Buscar cuando los resultados no tienen enlaces (el peor caso) | 274 ms |
| Exportar 200 objetos para sincronizar | 63 ms |

Nada de eso se nota en la mesa. La prueba corre en cada construcción con límites generosos: no está para medir lo rápido que es este computador, sino para avisar si algo se vuelve cuadrático.

## Lo que se arregló al medir

La búsqueda leía **todos los enlaces de la campaña una vez por resultado** para decir dónde vive cada cosa. Con 60 resultados eso eran 60 lecturas completas. Ahora los lee una sola vez por búsqueda. Antes del arreglo el peor caso ya costaba más que el caso normal; después, cuesta menos (274 ms contra 343 ms), porque hace menos trabajo por resultado.

## Si alguna vez molesta

En orden, lo que haría primero:

1. **Buscar en la base, no en memoria.** Hoy la búsqueda lee los objetos y compara el texto en la App. SQLite puede filtrar por nombre con un índice antes de que lleguen.
2. **Listas por páginas.** Las pantallas de Preparar traen todos los objetos de la familia; con varios miles conviene traerlos de a tramos mientras se desplaza.
3. **Guardar el resumen de cada objeto.** El resumen que muestra la búsqueda se arma leyendo el contenido; podría quedar escrito al guardar.

Ninguna hace falta todavía.
