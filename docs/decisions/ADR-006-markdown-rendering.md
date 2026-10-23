# ADR-006 — Renderizado de Markdown en respuestas

- **Estado:** Aceptado
- **Fecha:** 2026-10-23

## Contexto

Las respuestas de Perplexity son Markdown. Actualmente se muestran en `JTextArea` plano.

## Problema

Renderizar Markdown completo en Swing es costoso; JCEF no está disponible en todos los entornos.

## Opciones consideradas

1. Parser ligero en Swing (negrita, listas, código en línea y bloques).
2. JCEF/HTML.
3. Mantener texto plano.

## Decisión

Parser ligero en Swing con bloques de código y botones Copiar/Insertar/Reemplazar. Sin JCEF.

## Justificación

- Compatible con 231–262.*.
- Riesgo y esfuerzo controlados.

## Consecuencias

- No se soporta Markdown completo (tablas, imágenes).
- Requiere tests del parser.
