# Estudio de Rediseño de UI — Perplexity Sonar Pro Assistant

## 1. Auditoría visual (basada en código)

### Estructura actual
- `PerplexityToolWindowPanel.kt:21`: `JPanel(BorderLayout())`.
- Zona norte (`topPanel`): botón "Settings" a la izquierda y panel de estado (API key, límite, gasto) a la derecha (`PerplexityToolWindowPanel.kt:38-47`).
- Zona central: `JScrollPane(chatPanel)` con `preferredSize = Dimension(500, 350)` (línea 110).
- Zona sur: `promptPanel` con `promptField` + `sendButton` (líneas 112-114).

### Hallazgos con evidencia
1. **Colores hardcodeados:** `LineBorder(Color.GRAY, 1)` en `addChatMessage` (línea 158); `Color.GRAY` no se adapta al tema.
2. **Tamaño fijo:** `Dimension(500, 350)` (línea 110) no respeta el tamaño del tool window ni HiDPI.
3. **Sin estados vacíos/error/carga:** No existe un panel de estado vacío ni indicador de carga persistente; los errores se muestran con `JOptionPane` (líneas 124-134).
4. **Renderizado plano:** La respuesta se muestra en `JTextArea` sin formato (líneas 161-165).
5. **Iconos:** `plugin.xml` usa `AllIcons.Actions.Lightning` (genérico) para el tool window.
6. **Espaciado:** `Box.createVerticalStrut(8)` (línea 182) y `FlowLayout` (línea 167) sin uso de `JBUI.insets`.
7. **Accesibilidad:** No hay mnemonics ni labels asociados; el contraste depende del tema pero los colores fijos lo rompen.
8. **Densidad:** Los botones "Insert in editor"/"Copy response" se repiten por cada mensaje (líneas 167-177), aumentando la densidad visual.

### Convenciones de JetBrains
- Usar `JBColor` para colores adaptativos, `JBUI.Borders`/`JBUI.insets` para espaciado, `UIUtil` para fuentes.
- Iconos SVG en `AllIcons` o recursos propios con variantes claro/oscuro.
- Estados: vacío, carga y error deben ser parte del componente, no diálogos modales.
- Tema Islands: evitar colores absolutos y bordes duros.

## 2. Alternativas de diseño

### Alternativa A — Pulido Swing (Mínima)
**Descripción:** Mantener la estructura actual pero migrar a `JBColor`, `JBUI`, iconos SVG y añadir estados vacío/carga/error.

**Wireframe (ASCII):**
```
+--------------------------------------------------+
| [⚙ Settings]   API Key: OK | Limit: 5.0 | Used  |
+--------------------------------------------------+
|                                                  |
|  (empty state: "Send a prompt to start")         |
|                                                  |
|  [Prompt bubble]                                 |
|  [Response bubble with Copy/Insert]              |
|                                                  |
+--------------------------------------------------+
| [prompt field........................] [Send]    |
+--------------------------------------------------+
```

**Componentes:** `JBColor`, `JBUI.Borders`, `JButton` con iconos, `JLabel` para estado vacío.
**Riesgos:** Bajo. **Esfuerzo:** S. **Qué se migra:** colores, bordes, iconos, estados.

### Alternativa B — Kotlin UI DSL + burbujas (Media)
**Descripción:** Rediseñar con Kotlin UI DSL, burbujas de chat, bloques de código con acciones y barra de contexto (archivo/lenguaje).

**Wireframe (Mermaid):**
```mermaid
flowchart TB
  A[Toolbar: Settings | Model | Usage] --> B[Context bar: file.kt · Kotlin]
  B --> C[Chat list (bubbles)]
  C --> D[Prompt input + Send]
```

**Componentes:** `panel {}` DSL, `JList` con renderer personalizado, `JTextArea` para código con botones.
**Riesgos:** Medio (migración completa del layout, tests de UI). **Esfuerzo:** M.

### Alternativa C — JCEF/HTML (Ambiciosa)
**Descripción:** Renderizar el chat en HTML/Markdown vía JCEF.
**Riesgos:** Alto: JCEF no disponible en todos los entornos (Remote Development, algunos IDEs), rendimiento, seguridad (XSS). **Esfuerzo:** L. **Decisión:** Descartar.

## 3. Recomendación

**Recomendada: Alternativa A (pulido Swing) para 1.2.0, con migración incremental hacia B en 1.3.0.**

Justificación: valor inmediato con riesgo bajo; mantiene compatibilidad 231–262.*; un desarrollador puede ejecutarlo en días. Se deja fuera a propósito: JCEF (C) y la migración completa a DSL (B) hasta que el pulido esté validado.

## 4. Guía de estilo mínima

- **Iconos:** SVG, tamaños 16 (tool window) y 20 (acciones), variantes claro/oscuro en `resources/icons/`.
- **Colores:** Solo `JBColor` o `UIUtil`; nunca `Color.GRAY` ni literales.
- **Espaciado:** `JBUI.insets(...)` y `JBUI.Borders`; evitar `Dimension` fijas.
- **Textos:** Tono claro y directo; mensajes de error accionables ("Configure the API key in Settings").
- **Estados:** Vacío, carga, error y desactivado deben estar representados en el componente, no solo en diálogos.
- **Tema:** Verificar en claro, oscuro e Islands.