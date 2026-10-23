# ADR-008 — Modelos soportados

- **Estado:** Propuesto
- **Fecha:** 2026-10-23

## Contexto

El modelo está hardcodeado a `sonar-pro` (`PerplexityClient.MODEL`).

## Verificación de modelos y precios

Fuente: documentación oficial de Perplexity (https://docs.perplexity.ai/docs/getting-started/pricing), consultada el 2026-10-23, y tablas de terceros (promptfoo). Modelos de chat completions (Sonar) disponibles:

| Modelo | Input ($/1M) | Output ($/1M) | Contexto | Cargos adicionales |
|---|---|---|---|---|
| sonar | 1 | 1 | 128k | Per-request search fee ($5–$12/1k según search_context_size) |
| sonar-pro | 3 | 15 | 200k | Per-request search fee ($6–$14/1k) |
| sonar-reasoning-pro | 2 | 8 | 128k | Per-request search fee ($6–$14/1k) |
| sonar-deep-research | 2 | 8 | 128k | Sin request fee; cobra citation/reasoning tokens y búsquedas ($5/1k) |

Streaming (SSE): No verificado directamente en la documentación consultada; la API es compatible con OpenAI, que soporta `stream`. Se deja como "No verificado".

## Problema

El selector de modelo y el cálculo de coste deben basarse en una tabla de constantes única; los cargos de búsqueda no se modelan actualmente.

## Opciones consideradas

1. Soportar solo sonar-pro (actual).
2. Soportar sonar, sonar-pro y sonar-reasoning-pro con tabla de precios.
3. Soportar todos los modelos incluyendo sonar-deep-research.

## Decisión

Soportar sonar, sonar-pro y sonar-reasoning-pro en 1.3.0; sonar-deep-research queda para más adelante. Los precios por modelo viven en una tabla de constantes única. Los cargos de búsqueda se documentan pero no se modelan en la estimación (coste aproximado).

## Justificación

- Riesgo controlado y compatibilidad con la API actual.
- Los cargos de búsqueda son impredecibles sin search_context_size.

## Consecuencias

- La estimación de coste es aproximada (solo tokens).
- FEAT-004 marca como "coste aproximado" para modelos no verificados.