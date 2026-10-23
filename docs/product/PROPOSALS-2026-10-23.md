# Propuestas de Producto — Perplexity Sonar Pro Assistant

**Fecha:** 2026-10-23  
**Rama:** `fix/audit-2026-10-23`

## Resumen ejecutivo

El plugin cubre el caso de uso básico (chat + envío de prompt + inserción). Las propuestas priorizan **valor inmediato con bajo riesgo**: contexto del editor, acciones rápidas, renderizado de Markdown y estados de UI. Se descartan o se dejan para más adelante las ideas de alto coste (streaming, JCEF, múltiples conversaciones) por el tamaño del equipo (un desarrollador) y el coste de compatibilidad 231–262.*.

## Tabla índice

| ID | Título | Frente | Esfuerzo | Riesgo | Decisión |
|---|---|---|---|---|---|
| FEAT-001 | Enviar contexto del editor (archivo/selección/lenguaje) | 1 | M | Bajo | Hacer ahora (1.2.0) |
| FEAT-002 | Acciones rápidas sobre selección (explicar/revisar/refactorizar/tests/docs) | 1 | M | Bajo | Hacer ahora (1.2.0) |
| FEAT-003 | Renderizado de Markdown con bloques de código copiables/insertables | 1 | M | Medio | Hacer ahora (1.2.0) |
| FEAT-004 | Selector de modelo (sonar / sonar-pro / sonar-reasoning) | 1 | S | Bajo | Siguiente versión (1.3.0) — No verificado |
| FEAT-005 | Panel de uso y avisos de límite | 1 | S | Bajo | Hacer ahora (1.2.0) |
| FEAT-006 | Persistencia opcional del historial | 1 | M | Medio | Siguiente versión (1.3.0) |
| FEAT-007 | Cancelación de request en curso | 1 | M | Medio | Siguiente versión (1.3.0) |
| FEAT-008 | Streaming de respuestas | 1 | L | Alto | Más adelante (2.0.0) |
| FEAT-009 | Onboarding de API key (primer uso) | 1 | S | Bajo | Hacer ahora (1.2.0) |
| TECH-001 | Migrar a corrutinas de IntelliJ para I/O | 2 | M | Medio | Siguiente versión (1.3.0) |
| TECH-002 | Tests de integración de plataforma | 2 | M | Bajo | Siguiente versión (1.3.0) |
| TECH-003 | Publicación en Marketplace (firmado, descripción, capturas) | 2 | M | Bajo | Hacer ahora (1.2.0) |
| UI-001 | Pulido Swing con JBUI/JBColor y estados | 3 | S | Bajo | Hacer ahora (1.2.0) |
| UI-002 | Rediseño con Kotlin UI DSL y burbujas | 3 | M | Medio | Siguiente versión (1.3.0) |
| UI-003 | Renderizado rico con JCEF | 3 | L | Alto | Descartar (motivo abajo) |

---

## Detalle de propuestas

### FEAT-001 — Enviar contexto del editor
- **Frente:** 1
- **Problema:** `SendSelectionAction.kt` ignora la selección real y usa `panel.getPromptText()` (corregido parcialmente: ahora usa `selectedText` si existe). No se envía lenguaje ni ruta.
- **Propuesta:** Añadir un menú/acción "Send File to Perplexity" que incluya ruta relativa, lenguaje detectado y contenido (o selección), con vista previa antes de enviar.
- **Beneficio:** El usuario no copia/pega manualmente; la IA recibe contexto útil.
- **Esfuerzo:** M · **Riesgo:** Bajo
- **Dependencias:** FEAT-003 (para mostrar el contexto).
- **Criterios de aceptación:** Acción disponible en EditorPopupMenu; vista previa editable; envío solo tras confirmación.
- **Decisión:** Hacer ahora (1.2.0).

### FEAT-002 — Acciones rápidas sobre selección
- **Frente:** 1
- **Problema:** Solo existe "Send Selection to Perplexity"; el usuario debe escribir el prompt.
- **Propuesta:** Submenú con prompts predefinidos (Explain, Review, Refactor, Tests, Docs) que reutilizan `PerplexityClient`.
- **Beneficio:** Flujo de un clic sobre código.
- **Esfuerzo:** M · **Riesgo:** Bajo
- **Dependencias:** FEAT-001.
- **Criterios de aceptación:** Al menos 5 acciones; prompts configurables en settings.
- **Decisión:** Hacer ahora (1.2.0).

### FEAT-003 — Renderizado de Markdown
- **Frente:** 1
- **Problema:** `PerplexityToolWindowPanel.addChatMessage` muestra la respuesta en `JTextArea` plano (líneas 156–187); no hay formato ni acciones sobre bloques de código.
- **Propuesta:** Renderizar Markdown (negrita, listas, bloques de código) con botones Copiar/Insertar/Reemplazar en cada bloque.
- **Beneficio:** Respuestas legibles y aplicables directamente.
- **Esfuerzo:** M · **Riesgo:** Medio (parser Markdown, compatibilidad).
- **Dependencias:** ADR-006.
- **Criterios de aceptación:** Bloques de código con botón; texto largo sin romper layout.
- **Decisión:** Hacer ahora (1.2.0).

### FEAT-004 — Selector de modelo
- **Frente:** 1
- **Problema:** El modelo está hardcodeado a `sonar-pro` (`PerplexityClient.MODEL`).
- **Propuesta:** ComboBox en el panel y en settings para elegir modelo.
- **Beneficio:** Flexibilidad (reasoning, modelos más baratos).
- **Esfuerzo:** S · **Riesgo:** Bajo
- **Dependencias:** No verificado — confirmar modelos disponibles en la API actual de Perplexity. (Verificado en ADR-008: sonar, sonar-pro, sonar-reasoning-pro; sonar-deep-research queda para más adelante.)
- **Criterios de aceptación:** Cambio de modelo persiste; coste se recalcula.
- **Decisión:** Siguiente versión (1.3.0).

### FEAT-005 — Panel de uso y avisos
- **Frente:** 1
- **Problema:** `updateStatus` muestra gasto pero no avisa al acercarse al límite.
- **Propuesta:** Aviso visual cuando el gasto supera el 80% del límite; tooltip con última request.
- **Beneficio:** Control de coste proactivo.
- **Esfuerzo:** S · **Riesgo:** Bajo
- **Dependencias:** BUG-004 (corregido).
- **Criterios de aceptación:** Aviso visible al superar 80%; desaparece al resetear.
- **Decisión:** Hacer ahora (1.2.0).

### FEAT-006 — Persistencia del historial
- **Frente:** 1
- **Problema:** El historial vive solo en memoria (`chatPanel`); se pierde al cerrar.
- **Propuesta:** Guardar historial en disco (XML/JSON) de forma opcional, con límite y borrado.
- **Beneficio:** Continuidad entre sesiones.
- **Esfuerzo:** M · **Riesgo:** Medio (privacidad, tamaño).
- **Dependencias:** ADR-007, consentimiento explícito.
- **Criterios de aceptación:** Opción en settings; borrado manual; no se guardan secretos.
- **Decisión:** Siguiente versión (1.3.0).

### FEAT-007 — Cancelación de request
- **Frente:** 1
- **Problema:** `ProgressManager.runProcessWithProgressSynchronously` no permite cancelar la llamada HTTP.
- **Propuesta:** Usar `Call.cancel()` de OkHttp vinculado al `ProgressIndicator`.
- **Beneficio:** Evita esperas innecesarias.
- **Esfuerzo:** M · **Riesgo:** Medio.
- **Dependencias:** TECH-001 (corrutinas facilitan).
- **Criterios de aceptación:** Botón Cancel detiene la request; no se registra coste.
- **Decisión:** Siguiente versión (1.3.0).

### FEAT-008 — Streaming
- **Frente:** 1
- **Problema:** La respuesta se muestra completa al final.
- **Propuesta:** Streaming SSE si la API lo soporta.
- **Beneficio:** Mejor UX en respuestas largas.
- **Esfuerzo:** L · **Riesgo:** Alto (API, parseo, cancelación).
- **Dependencias:** FEAT-007, ADR-006.
- **Criterios de aceptación:** Texto aparece progresivamente; cancelable.
- **Decisión:** Más adelante (2.0.0) o Descartar si la API no lo soporta.

### FEAT-009 — Onboarding de API key
- **Frente:** 1
- **Problema:** Si no hay key, el usuario ve un error genérico (`SendSelectionAction.kt:46-48`).
- **Propuesta:** Al primer uso, mostrar un diálogo que explique cómo obtener la key y abra settings.
- **Beneficio:** Reduce fricción inicial.
- **Esfuerzo:** S · **Riesgo:** Bajo.
- **Dependencias:** —
- **Criterios de aceptación:** Diálogo solo la primera vez; enlace a settings.
- **Decisión:** Hacer ahora (1.2.0).

### TECH-001 — Corrutinas de IntelliJ
- **Frente:** 2
- **Problema:** `ProgressManager.runProcessWithProgressSynchronously` bloquea el EDT y complica la cancelación.
- **Propuesta:** Migrar I/O a `CoroutineScope`/`withContext(Dispatchers.IO)` con progreso.
- **Beneficio:** Mejor responsividad y cancelación.
- **Esfuerzo:** M · **Riesgo:** Medio (compatibilidad 231).
- **Dependencias:** FEAT-007.
- **Criterios de aceptación:** Sin regresiones; tests pasan.
- **Decisión:** Siguiente versión (1.3.0).

### TECH-002 — Tests de integración de plataforma
- **Frente:** 2
- **Problema:** Los 14 tests son unitarios puros; no cubren UI ni servicios de plataforma.
- **Propuesta:** Añadir tests con `TestFrameworkType.Platform` para `PerplexitySettingsState` persistencia y `PerplexityClient` con mock HTTP.
- **Beneficio:** Mayor confianza en cambios de UI.
- **Esfuerzo:** M · **Riesgo:** Bajo.
- **Dependencias:** —
- **Criterios de aceptación:** Tests ejecutables en CI local; sin dependencia de red.
- **Decisión:** Siguiente versión (1.3.0).

### TECH-003 — Publicación en Marketplace
- **Frente:** 2
- **Problema:** No hay configuración de firma/publicación ni descripción oficial.
- **Propuesta:** Configurar `signPlugin`/`publishPlugin`, descripción, changelog y capturas.
- **Beneficio:** Distribución oficial.
- **Esfuerzo:** M · **Riesgo:** Bajo.
- **Dependencias:** —
- **Criterios de aceptación:** Plugin verificado y publicable.
- **Decisión:** Hacer ahora (1.2.0).

### UI-001 — Pulido Swing con JBUI/JBColor
- **Frente:** 3
- **Problema:** La UI usa `Color.GRAY`, `LineBorder`, tamaños fijos (`Dimension(500, 350)`), strings hardcodeados.
- **Propuesta:** Migrar a `JBColor`, `JBUI.Borders`, `JBUI.insets`, `UIUtil`, iconos SVG.
- **Beneficio:** Coherencia con el tema del IDE y HiDPI.
- **Esfuerzo:** S · **Riesgo:** Bajo.
- **Dependencias:** —
- **Criterios de aceptación:** Sin colores hardcodeados; correcto en tema oscuro.
- **Decisión:** Hacer ahora (1.2.0).

### UI-002 — Rediseño con Kotlin UI DSL
- **Frente:** 3
- **Problema:** Layout Swing manual difícil de mantener.
- **Propuesta:** Rediseñar con Kotlin UI DSL y burbujas de chat.
- **Beneficio:** Mejor jerarquía visual y mantenibilidad.
- **Esfuerzo:** M · **Riesgo:** Medio.
- **Dependencias:** UI-001.
- **Criterios de aceptación:** Paridad funcional; tests pasan.
- **Decisión:** Siguiente versión (1.3.0).

### UI-003 — Renderizado rico con JCEF
- **Frente:** 3
- **Problema:** Markdown complejo es difícil en Swing.
- **Propuesta:** Usar JCEF para renderizar HTML/Markdown.
- **Beneficio:** Renderizado rico.
- **Esfuerzo:** L · **Riesgo:** Alto (JCEF no disponible en todos los entornos; rendimiento).
- **Dependencias:** —
- **Criterios de aceptación:** Fallback si JCEF no disponible.
- **Decisión:** Descartar (riesgo y mantenimiento desproporcionados para un desarrollador).

---

## Roadmap por versiones

### 1.2.0 (alcance realista)
- FEAT-001, FEAT-002, FEAT-003, FEAT-005, FEAT-009, TECH-003, UI-001.

### 1.3.0
- FEAT-004, FEAT-006, FEAT-007, TECH-001, TECH-002, UI-002.

### 2.0.0
- FEAT-008 (si la API lo soporta). UI-003 descartado.