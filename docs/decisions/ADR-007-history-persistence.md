# ADR-007 — Persistencia del historial de chat

- **Estado:** Propuesto
- **Fecha:** 2026-10-23

## Contexto

El historial vive solo en memoria y se pierde al cerrar el IDE.

## Problema

Persistir conversaciones implica privacidad, tamaño y compatibilidad con git.

## Opciones consideradas

1. Sin persistencia (actual).
2. Persistencia opt-in en el directorio de datos del plugin, con límites y borrado.
3. Persistencia en el proyecto (riesgo de commitear datos).

## Decisión

Persistencia opt-in en el directorio de datos del plugin, desactivada por defecto, con límite de mensajes/tamaño y botón de borrado. No guardar API keys ni contenido de archivos si se decide excluirlo (a documentar).

## Justificación

- Evita contaminar el repositorio.
- Control de tamaño y borrado.

## Consecuencias

- Requiere consentimiento explícito y aviso de privacidad.
- Complejidad adicional de serialización.