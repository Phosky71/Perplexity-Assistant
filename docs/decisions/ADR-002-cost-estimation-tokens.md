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

## Consecuencias

- La estimación puede desviarse de la factura real si cambian los precios.
- Requiere que la respuesta incluya `usage`; si no, el coste se registra como 0.0.