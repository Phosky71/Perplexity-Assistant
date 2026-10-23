# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.2.0] - 2026-10-23

### Added
- Pulido de UI Swing con `JBUI`/`JBColor` y estado vacío (UI-001).
- Onboarding de API key con aviso de privacidad (FEAT-009).
- Aviso de uso al 80% del límite mensual y tooltip de última request (FEAT-005).
- Acción "Send File Context to Perplexity" con vista previa (FEAT-001).
- Acciones rápidas Explain/Review/Refactor/Tests/Docs (FEAT-002).
- Renderizado ligero de Markdown con bloques de código y acciones (FEAT-003).
- Configuración de firma/publicación en Marketplace (TECH-003).

### Changed
- Versión a 1.2.0.

### Security
- La API key se gestiona exclusivamente a través de `PasswordSafe`.

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