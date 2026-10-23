# ADR-003 — Límite mensual basado en coste real estimado

- **Estado:** Propuesto
- **Fecha:** 2026-10-23

## Contexto

Actualmente `PerplexitySettingsState.canMakeRequest()` valida el límite usando `estimatedCostPerRequestUsd` (valor estático, por defecto 0.02), mientras que el coste real se registra después de la llamada mediante `registerRequest(cost)`. Esto desacopla la validación del consumo real.

## Problema

El límite mensual se evalúa con un coste fijo arbitrario, permitiendo más o menos requests de las debidas y generando una falsa sensación de control.

## Opciones consideradas

1. Mantener coste fijo por request (actual).
2. Validar con el coste estimado real a partir de tokens (requiere estimación previa o validación post-llamada).
3. Validar post-llamada y cancelar/advertir si se supera (no bloquea antes).

## Decisión

(Propuesta) La validación previa usa una estimación conservadora (prompt estimado + max_tokens configurado) y después se reconcilia con el `usage` real; fallback a `estimatedCostPerRequestUsd`. Cambiar `canMakeRequest(estimatedCost: Double)` y pasar el coste estimado antes de la llamada.

## Consecuencias

- Cambio de firma en múltiples call sites.
- Mejor precisión del control de gasto.