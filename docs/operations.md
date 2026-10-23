# Operaciones — Perplexity Sonar Pro Assistant

## Build y distribución

- Build: `./gradlew buildPlugin` (Windows: `gradlew.bat buildPlugin`).
- Artefacto: `build/distributions/*.zip`.
- Instalación manual: `Settings > Plugins > Install Plugin from Disk`.

## Compatibilidad

- IntelliJ IDEA 2023.1+ (since-build `231`), hasta `262.*`.
- Requiere Java 17+.

## Configuración en runtime

- `Settings > Tools > Perplexity Assistant` (o diálogo embebido en el tool window).
- API key de Perplexity obligatoria para enviar requests.

## Observabilidad

- Errores de llamada a la API se registran mediante `Logger` (IntelliJ) y se muestran al usuario como mensaje de error.