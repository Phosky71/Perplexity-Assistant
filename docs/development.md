# Desarrollo — Perplexity Sonar Pro Assistant

## Requisitos

- JDK 17+
- Gradle (wrapper incluido)
- IntelliJ IDEA 2023.1+ (o IDE compatible JetBrains)

## Comandos

```bash
# Compilar el plugin
./gradlew buildPlugin

# El zip resultante queda en build/distributions/
```

En Windows usar `gradlew.bat`.

## Estructura del código

```
src/main/kotlin/com/phosky/antoniojuan/perplexityassistant/
├── actions/        # Acciones del IDE (SendSelectionAction, OpenSettingsAction stub)
├── settings/       # Estado persistente, configurable y credenciales
└── ui/             # Tool window factory y panel principal
src/main/resources/
├── META-INF/plugin.xml
└── messages/PluginBundle.properties
```

## Ejecutar en modo desarrollo

Usar la configuración de run incluida (`.run/Run IDE with Plugin.run.xml`) o ejecutar la tarea de Gradle `runIde` si está disponible en el plugin de IntelliJ Platform.

## Convenciones

- Kotlin con target JVM 17.
- Identificadores y términos técnicos en inglés; documentación en español.
- No hardcodear API keys ni secretos.