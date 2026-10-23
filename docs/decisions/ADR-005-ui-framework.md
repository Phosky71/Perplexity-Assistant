# ADR-005 — Framework de UI para el plugin

- **Estado:** Propuesto
- **Fecha:** 2026-10-23

## Contexto

La UI actual es Swing manual (`PerplexityToolWindowPanel`). Se evalúa migrar a Kotlin UI DSL o mantener Swing pulido.

## Problema

Un desarrollador en solitario debe equilibrar mejora visual con compatibilidad 231–262.* y coste de mantenimiento.

## Opciones consideradas

1. Mantener Swing y pulir con `JBUI`/`JBColor`.
2. Migrar a Kotlin UI DSL.
3. Renderizar con JCEF/HTML.

## Decisión

Mantener Swing pulido con `JBUI`/`JBColor` en 1.2.0 y migrar incrementalmente a Kotlin UI DSL en 1.3.0. No usar JCEF.

## Justificación

- Riesgo bajo y compatibilidad amplia.
- Esfuerzo controlado para un desarrollador.

## Consecuencias

- Se mantiene código Swing manual.
- La migración a DSL queda para una versión posterior.