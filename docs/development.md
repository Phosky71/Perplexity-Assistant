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

## Mock API para desarrollo y pruebas

El plugin permite sobreescribir la URL de la API **solo en desarrollo** mediante la propiedad de sistema `perplexity.api.url`. Por seguridad, **solo se aceptan hosts `localhost` o `127.0.0.1`** (http/https). Cualquier otro host se ignora y se registra un aviso.

### Arranque del mock

```bash
# Levantar el mock server (ejemplo con mockoon, wiremock, o similar en puerto 8765)
# Luego arrancar el IDE con la propiedad:
./gradlew runIde -PmockApi
```

Esto pasa `-Dperplexity.api.url=http://127.0.0.1:8765/chat/completions` a la JVM del IDE de desarrollo.

### Modos del mock (MOCK_MODE)

El mock server puede simular distintos comportamientos mediante la variable de entorno `MOCK_MODE`:

| Modo | Descripción | Código HTTP | Cuerpo de respuesta |
|------|-------------|-------------|---------------------|
| `ok` | Respuesta exitosa estándar | 200 | `{"choices":[{"message":{"content":"Mock response"}}],"usage":{"prompt_tokens":10,"completion_tokens":5}}` |
| `401` | API key inválida | 401 | `{"error":{"message":"Unauthorized"}}` |
| `429` | Rate limit excedido | 429 | `{"message":"Rate limit exceeded"}` |
| `500` | Error interno del servidor | 500 | `Internal Server Error` |
| `empty` | Respuesta 200 sin choices | 200 | `{"choices":[]}` |
| `nousage` | Respuesta 200 sin objeto usage | 200 | `{"choices":[{"message":{"content":"OK"}}]}` |

Ejemplo con `MOCK_MODE=429`:
```bash
MOCK_MODE=429 ./mock-server-start-script
```

### Retraso simulado (MOCK_DELAY)

Para probar timeouts y latencia, definir `MOCK_DELAY` en milisegundos:
```bash
MOCK_DELAY=2000 ./mock-server-start-script  # 2 segundos de retraso
```

### Ejemplo completo con mockoon

```json
// mockoon-environment.json
{
  "routes": [
    {
      "method": "post",
      "endpoint": "/chat/completions",
      "response": {
        "body": "{{#if (eq (env 'MOCK_MODE') '401')}}...{{/if}}",
        "statusCode": "{{#if (eq (env 'MOCK_MODE') '401')}}401{{else}}200{{/if}}"
      }
    }
  ]
}
```

## Convenciones

- Kotlin con target JVM 17.
- Identificadores y términos técnicos en inglés; documentación en español.
- No hardcodear API keys ni secretos.