# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.3.0] - 2026-10-23

### Added
- Selector de modelo (sonar, sonar-pro, sonar-reasoning-pro) con tabla de precios por modelo.
- Historial de chat opcional (opt-in) con almacenamiento local y borrado.
- Cancelación de requests en curso.
- Tests de plataforma (MockWebServer, persistencia round-trip).

### Changed
- I/O mediante ProgressManager con cancelación.
- Versión a 1.3.0.

### Security
- La API key se gestiona exclusivamente a través de `PasswordSafe`.

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
- Versión inicial del plugin.

### Changed
- Compatibilidad hasta 262.*.