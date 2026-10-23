# Seguridad — Perplexity Sonar Pro Assistant

## Gestión de la API key

- La API key se almacena mediante `PasswordSafe` (`PerplexityCredentials`, servicio `PerplexitySonarProPlugin.ApiKey`).
- No se persiste en el XML de estado ni se registra en logs.
- En la UI se muestra enmascarada (`JPasswordField`).

## Datos y privacidad

- Los prompts y el código enviado se transmiten a la API de Perplexity (`https://api.perplexity.ai/chat/completions`).
- No se almacenan conversaciones en disco; el historial vive solo en memoria del panel.
- El gasto se estima localmente a partir de los tokens reportados por la API.

## Límites y control de coste

- `PerplexitySettingsState.canMakeRequest()` bloquea requests cuando el gasto acumulado + coste estimado supera el límite mensual configurado.
- El contador se resetea automáticamente al cambiar de mes (`YearMonth`).
- Existe opción manual de reset en el diálogo de settings.

## Consideraciones

- La API key es responsabilidad del usuario; el plugin no valida su formato.
- No se implementa autenticación adicional ni cifrado propio más allá de `PasswordSafe`.