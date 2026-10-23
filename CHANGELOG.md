# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Plugin IntelliJ "Perplexity Sonar Pro Assistant": tool window de chat con historial visual, inserción y copia de respuestas.
- Integración con la API de Perplexity (`sonar-pro`) mediante OkHttp, con timeouts de conexión/lectura/escritura.
- Acción de editor `Send Selection to Perplexity` (atajo `Ctrl+Alt+P`) con envío de prompt y selección.
- Configuración de aplicación (`Settings > Tools > Perplexity Assistant`): API key, prompt base, coste estimado por request y límite mensual en USD.
- Almacenamiento seguro de la API key mediante `PasswordSafe` (`PerplexityCredentials`).
- Estimación de coste por tokens (input/output) y visualización de citas en la respuesta.
- `README.md` y licencia MIT.

### Changed
- Traducción de etiquetas de UI y mensajes a inglés.
- Actualización de `plugin.xml`: nombre comercial "Perplexity Sonar Pro Assistant", versión `1.1.0`, compatibilidad `since-build 231` / `until-build 262.*`.
- Actualización de `build.gradle.kts`: plugin IntelliJ Platform 2.7.1, Kotlin 2.1.0, dependencia OkHttp 4.12.0, IDE base IC 2023.1.
- Refactor de `PerplexityToolWindowPanel` para invocar la API directamente desde el panel y mostrar estado de uso mensual.

### Fixed
- Anotación `@Service(Service.Level.APP)` y persistencia de `PerplexitySettingsState` (componente `PersistentStateComponent`).
- Serialización JSON de `messages` mediante `JSONArray`/`JSONObject` en `SendSelectionAction` y `PerplexityToolWindowPanel`.
- Timeouts HTTP configurados (30s conexión, 60s lectura, 30s escritura).
- Bug en `canMakeRequest()`: eliminación de `isMonthStarted` que provocaba falsos "límite alcanzado"; reseteo automático por cambio de mes.
- Registros faltantes en `plugin.xml` (application service y configurable).

### Security
- La API key se gestiona exclusivamente a través de `PasswordSafe`; no se hardcodea ni se registra en logs.

### Changed
- Documentación: auditoría de código y seguridad registrada en `docs/audit/AUDIT-2026-10-23.md` (sin cambios de código).

### Fixed
- Núcleo HTTP compartido (`PerplexityClient`), control de gasto sincronizado, ciclo de vida del panel, inserción en editor activo, deshabilitado de Send, límite de historial, consolidación de settings, gitignore de `.intellijPlatform`.

## [1.1.0] - 2025-11-25

### Added
- Versión de release inicial del plugin (1.1.0).

### Changed
- Consolidación de mejoras de JSON, HTTP y compatibilidad previas a la release.
