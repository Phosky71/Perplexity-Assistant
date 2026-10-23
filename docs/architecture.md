# Arquitectura — Perplexity Sonar Pro Assistant

## Visión general

Plugin para la plataforma IntelliJ que integra la API de Perplexity (`sonar-pro`) en un tool window de chat dentro del IDE. Permite enviar prompts (y opcionalmente texto seleccionado) a Perplexity, visualizar la respuesta, copiarla o insertarla en el editor, y controlar el gasto mensual estimado.

## Componentes principales

- `com.phosky.antoniojuan.perplexityassistant.actions.SendSelectionAction`
  - Acción de editor registrada en `EditorPopupMenu` (atajo `Ctrl+Alt+P`).
  - Lee el prompt del panel, valida API key y límite mensual, ejecuta la llamada HTTP con `ProgressManager` y muestra la respuesta en el panel.
- `com.phosky.antoniojuan.perplexityassistant.ui.PerplexityToolWindowFactory`
  - Factory del tool window "Perplexity" (anchor right). Mantiene instancia estática de `PerplexityToolWindowPanel`.
- `com.phosky.antoniojuan.perplexityassistant.ui.PerplexityToolWindowPanel`
  - UI principal (Swing): barra de estado (API key, límite, gasto), área de chat con historial, campo de prompt y botón Send.
  - Diálogo de configuración embebido (API key, límite mensual, reset de gasto).
  - Lógica de llamada a la API (`callPerplexityApiWithCost`) incluyendo parseo de `usage`, coste estimado y citas.
- `com.phosky.antoniojuan.perplexityassistant.settings.PerplexitySettingsState`
  - `PersistentStateComponent` a nivel aplicación (`@Service(Service.Level.APP)`), persistido en `perplexity-assistant.xml`.
  - Almacena prompt base, coste estimado por request, límite mensual, gasto acumulado y mes actual.
  - `canMakeRequest()` resetea el contador al cambiar de mes.
- `com.phosky.antoniojuan.perplexityassistant.settings.PerplexitySettingsConfigurable`
  - Configurable de IDE (`Settings > Tools > Perplexity Assistant`), alternativo al diálogo embebido.
- `com.phosky.antoniojuan.perplexityassistant.settings.PerplexityCredentials`
  - Objeto para guardar/leer la API key en `PasswordSafe` (servicio `PerplexitySonarProPlugin.ApiKey`).

## Flujo de datos

1. El usuario introduce un prompt en el panel (o usa la acción de selección, que reutiliza el prompt del panel).
2. Se valida API key (`PerplexityCredentials`) y límite mensual (`PerplexitySettingsState.canMakeRequest()`).
3. Se construye el payload JSON con `messages` (system prompt base + user prompt) y se envía a `https://api.perplexity.ai/chat/completions` con modelo `sonar-pro`.
4. La respuesta se parsea (`choices[0].message.content`, `usage`, `citations`) y se muestra en el chat; se registra el coste estimado (`registerRequest`).

## Dependencias externas

- IntelliJ Platform SDK (IC 2023.1+, hasta 262.*).
- `org.json:json:20231013` para serialización JSON.
- `com.squareup.okhttp3:okhttp:4.12.0` para HTTP.
- API de Perplexity (requiere API key del usuario).

## Decisiones de diseño destacadas

- La API key nunca se persiste en XML; se usa `PasswordSafe`.
- El gasto se estima a partir de tokens reportados por la API (`$3/1M input`, `$15/1M output` para sonar-pro).
- El panel mantiene una instancia estática (`PerplexityToolWindowFactory.instance`) para que la acción de editor pueda acceder a él.