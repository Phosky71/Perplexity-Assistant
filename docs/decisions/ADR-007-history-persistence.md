# ADR-007 — Persistencia del historial de chat

- **Estado:** Propuesto
- **Fecha:** 2026-10-23

## Contexto

El historial vive solo en memoria y se pierde al cerrar el IDE.

## Decisión (propuesta)

Persistencia opcional en disco (XML/JSON) con límite configurable y borrado manual, desactivada por defecto por privacidad. Requiere consentimiento explícito del usuario.