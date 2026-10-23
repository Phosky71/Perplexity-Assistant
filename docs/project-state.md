# Estado del Proyecto — Perplexity Sonar Pro Assistant

## Identidad

Plugin de IntelliJ que integra Perplexity AI (`sonar-pro`) en un tool window de chat. Versión actual: `1.1.0` (en desarrollo `1.2.0`).

## Estado actual

- Funcionalidad core operativa; auditoría 2026-10-23 corregida (24/25).
- UI y mensajes en inglés.
- Persistencia de settings y API key segura.
- Compatibilidad hasta IDE 262.*.

## Cambios recientes

- Núcleo HTTP compartido (`PerplexityClient`), control de gasto sincronizado, ciclo de vida del panel.
- Inserción en editor activo, botón Send deshabilitado durante request, límite de historial.
- Consolidación de settings y PasswordSafe fuera del EDT.
- Documentación: propuestas de producto y rediseño de UI.

## Decisiones recientes

- ADR-001 (Aceptado): API key con PasswordSafe.
- ADR-002 (Aceptado): Estimación de coste por tokens.
- ADR-003 (Propuesto): Límite mensual basado en coste real estimado (implementación parcial: `canMakeRequest(estimatedCost)` con fallback).
- ADR-004 (Aceptado): Consolidación de la UI de settings (commit 7fed4cc).
- ADR-005 (Propuesto): Framework de UI.
- ADR-006 (Propuesto): Renderizado de Markdown.
- ADR-007 (Borrador): Persistencia del historial.
- ADR-008 (Borrador): Modelos soportados.

## Trabajo pendiente

- Implementación de 1.2.0 (UI-001, FEAT-009, FEAT-005, FEAT-001, FEAT-002, FEAT-003, TECH-003).
- BUG-014: No verificado (pricing oficial).

## Sesiones OpenCode

### 2026-10-23 — Flujo de documentación Operia (operia-docs-flow) y auditoría
`opencode -s ses_e97e32996ffebXgInwPBCyR72U`

### 2026-10-23 — Estudio de producto y rediseño UI
`opencode -s ses_e97e32996ffebXgInwPBCyR72U`