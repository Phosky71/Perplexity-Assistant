# ADR-001 — Almacenamiento de la API key con PasswordSafe

- **Estado:** Aceptado
- **Fecha:** 2025-11-25

## Contexto

El plugin requiere una API key de Perplexity para funcionar. Era necesario decidir dónde persistirla.

## Problema

Guardar la API key en el XML de settings (`perplexity-assistant.xml`) la expondría en texto plano en el sistema de archivos del usuario.

## Opciones consideradas

1. XML de settings (texto plano).
2. `PasswordSafe` de IntelliJ (almacén seguro del IDE).
3. Variable de entorno.

## Decisión

Usar `PasswordSafe` mediante `PerplexityCredentials` (servicio `PerplexitySonarProPlugin.ApiKey`).

## Justificación

- Aprovecha el almacén seguro nativo del IDE (keychain/credential store del SO).
- Evita secretos en repositorio y en archivos de configuración versionados.
- Integración sencilla con la API de IntelliJ.

## Consecuencias

- La API key no se sincroniza entre máquinas automáticamente.
- Depende de la disponibilidad de `PasswordSafe` en el IDE host.