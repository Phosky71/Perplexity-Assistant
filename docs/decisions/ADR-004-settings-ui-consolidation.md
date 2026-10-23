# ADR-004 — Consolidación de la UI de settings

- **Estado:** Propuesto
- **Fecha:** 2026-10-23

## Contexto

Existen dos UIs de configuración: `PerplexitySettingsConfigurable` (IDE Settings) y un diálogo embebido en `PerplexityToolWindowPanel`. Tienen capacidades distintas (base prompt/coste por request solo en el configurable; reset de gasto solo en el panel).

## Problema

Duplicación y divergencia de funcionalidad, generando confusión y estado inconsistente.

## Opciones consideradas

1. Mantener ambas (actual).
2. Consolidar todo en `PerplexitySettingsConfigurable` y que el panel abra el configurable.
3. Consolidar todo en el diálogo del panel y eliminar el configurable.

## Decisión

(Propuesta) Consolidar en `PerplexitySettingsConfigurable` (opción 2) y hacer que el botón "Settings" del panel abra el diálogo de settings del IDE, eliminando el diálogo embebido.

## Consecuencias

- Eliminación de código duplicado.
- Requiere habilitar `OpenSettingsAction` o usar `ShowSettingsUtil`.