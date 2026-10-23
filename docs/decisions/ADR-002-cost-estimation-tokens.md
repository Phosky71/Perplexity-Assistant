# ADR-002 — Estimación de coste por tokens reportados por la API

- **Estado:** Aceptado
- **Fecha:** 2025-11-25

## Contexto

Se necesita controlar el gasto mensual del usuario frente a la API de Perplexity.

## Problema

La API no devuelve coste monetario directo; solo tokens de entrada/salida.

## Opciones consideradas

1. Coste fijo por request (configurable).
2. Estimación basada en tokens reportados y precios públicos de sonar-pro.

## Decisión

Estimar el coste como `(prompt_tokens * 0.000003) + (completion_tokens * 0.000015)` (precios sonar-pro: $3/1M input, $15/1M output), usando los valores de `usage` de la respuesta.

## Justificación

- Refleja mejor el consumo real que un coste fijo.
- Permite bloquear requests cuando se supera el límite mensual configurado.

## Nota (1.3.0)

Los precios por modelo difieren (ver ADR-008). La tabla de coste debe ser por modelo; la fórmula base se mantiene. Los cargos de búsqueda no se modelan.