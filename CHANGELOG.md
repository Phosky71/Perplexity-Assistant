# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Estudio de producto y rediseño de UI en `docs/product/` (propuestas y alternativas A/B/C).
- ADR-005..008 propuestos.

### Changed
- Núcleo HTTP compartido (`PerplexityClient`), consolidación de settings y pulido de UI en curso para 1.2.0.
- Documentación: auditoría de código y seguridad registrada en `docs/audit/AUDIT-2026-10-23.md`.

### Fixed
- Control de gasto sincronizado (`PerplexitySettingsState`), registro de coste en ambas vías (panel y acción de editor).
- Serialización JSON de `messages` mediante `JSONArray`/`JSONObject`.
- Timeouts HTTP configurados (30s conexión, 60s lectura, 30s escritura).
- Bug en `canMakeRequest()`: eliminación de `isMonthStarted`.
- Registros faltantes en `plugin.xml` (application service y configurable).
- Inserción de texto en editor activo (`FileEditorManager.selectedTextEditor`).
- Eliminación de instancia estática del panel y de código muerto (`OpenSettingsAction`).

### Security
- La API key se gestiona exclusivamente a través de `PasswordSafe`; no se hardcodea ni se registra en logs.

## [1.1.0] - 2025-11-25

### Added
- Versión inicial del plugin "Perplexity Sonar Pro Assistant" (tool window de chat, envío de prompt, inserción/copia de respuestas, control de gasto mensual).
- Integración con la API de Perplexity (`sonar-pro`) mediante OkHttp.
- Acción de editor `Send Selection to Perplexity` (atajo `Ctrl+Alt+P`).
- Configuración de aplicación (API key, prompt base, coste estimado, límite mensual).
- `README.md` y licencia MIT.

### Changed
- Traducción de etiquetas de UI y mensajes a inglés.
- Actualización de `plugin.xml` y `build.gradle.kts` (compatibilidad hasta 262.*).