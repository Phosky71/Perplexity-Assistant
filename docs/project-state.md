# Estado del Proyecto — Perplexity Sonar Pro Assistant

## Identidad

Plugin de IntelliJ que integra Perplexity AI (`sonar-pro`) en un tool window de chat. Versión actual: `1.1.0`.

## Estado actual

- Funcionalidad core operativa: chat, envío de prompt, inserción/copia de respuesta, control de gasto mensual.
- UI y mensajes en inglés.
- Persistencia de settings y API key segura.
- Compatibilidad hasta IDE 262.*.

## Cambios recientes

- Consolidación de correcciones de serialización JSON, timeouts HTTP y anotación de servicio.
- Eliminación del bug `isMonthStarted` en `canMakeRequest()`.
- Añadida dependencia OkHttp y actualización de `untilBuild`.

## Decisiones recientes

- ADR-001 (Aceptado): API key con PasswordSafe.
- ADR-002 (Aceptado): Estimación de coste por tokens reportados por la API.
- ADR-003 (Propuesto): Límite mensual basado en coste real estimado (estimación conservadora + reconciliación).
- ADR-004 (Propuesto): Consolidación de la UI de settings.

## Trabajo pendiente

Todos los hallazgos de la auditoría 2026-10-23 han sido corregidos salvo BUG-014 (No verificado, pricing oficial de sonar-pro). Ver `docs/audit/AUDIT-2026-10-23.md` sección 9.

## Sesiones OpenCode

### 2026-10-23 — Flujo de documentación Operia (operia-docs-flow) y auditoría
`opencode -s ses_e97e32996ffebXgInwPBCyR72U`