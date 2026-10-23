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

## Trabajo pendiente / deuda técnica

- `OpenSettingsAction` está comentado/deshabilitado (TODO).
- El diálogo de settings está duplicado (configurable IDE + diálogo embebido); consolidar.
- No hay tests automatizados.
- El historial de chat se pierde al cerrar el IDE (solo en memoria).
- `registerRequest(cost)` se invoca en el panel pero no en `SendSelectionAction` (coste no contabilizado en esa vía).
- Inserción de texto usa el primer editor encontrado, no necesariamente el editor activo.

## Decisiones recientes

- Uso de `PasswordSafe` para la API key (ADR-001).
- Estimación de coste basada en tokens reportados (ADR-002).

## Sesiones OpenCode

### 2026-10-23 — Flujo de documentación Operia (operia-docs-flow)
`opencode -s ses_e97e32996ffebXgInwPBCyR72U`

- Habilitar `OpenSettingsAction` o eliminarlo.
- Añadir tests unitarios para `PerplexitySettingsState.canMakeRequest()`.
- Registrar coste también en `SendSelectionAction`.
- Persistir historial de chat opcionalmente.