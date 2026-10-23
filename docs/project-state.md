# Estado del Proyecto — Perplexity Sonar Pro Assistant

## Identidad

Plugin de IntelliJ que integra Perplexity AI en un tool window de chat. Versión actual: `1.3.0`.

## Estado actual

- Funcionalidad core operativa: chat, contexto del editor, acciones rápidas, Markdown, panel de uso, selector de modelo, historial opcional, cancelación.
- UI y mensajes en inglés.
- Persistencia de settings y API key segura.
- Compatibilidad verificada con IDE 231, 241, 252.
- No publicado en JetBrains Marketplace (pendiente).

## Cambios recientes

- Merge de `feat/1.3.0` en `master` (release 1.3.0).
- Historial opcional, selector de modelo, cancelación, tests de plataforma.
- README reconciliado con estado real.

## Decisiones recientes

- ADR-001 (Aceptado): API key con PasswordSafe.
- ADR-002 (Aceptado): Estimación de coste por tokens (por modelo en ADR-008).
- ADR-003 (Propuesto): Límite mensual basado en coste real estimado (implementación parcial).
- ADR-004 (Aceptado): Consolidación de la UI de settings.
- ADR-005 (Aceptado): Framework de UI (Swing pulido).
- ADR-006 (Aceptado): Renderizado de Markdown.
- ADR-007 (Aceptado): Persistencia del historial (opt-in).
- ADR-008 (Aceptado): Modelos soportados.

## Trabajo pendiente

- UI-002 (Kotlin UI DSL) aplazado a 1.4.0.
- BUG-014: parcial (precios de tokens verificados; cargos de búsqueda no modelados).
- Publicación en Marketplace (TECH-003) pendiente de decisión de nombre.

## Sesiones OpenCode

### 2026-10-23 — Flujo de documentación Operia (operia-docs-flow) y auditoría
`opencode -s ses_e97e32996ffebXgInwPBCyR72U`

### 2026-10-23 — Estudio de producto y rediseño UI
`opencode -s ses_e97e32996ffebXgInwPBCyR72U`

### 2026-10-23 — Release 1.3.0 y sincronización master
`opencode -s ses_e97e32996ffebXgInwPBCyR72U`