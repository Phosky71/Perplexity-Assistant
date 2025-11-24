package com.phosky.antoniojuan.perplexityassistant.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import java.time.YearMonth

@State(
    name = "PerplexitySettingsState",
    storages = [Storage("perplexity-sonar-pro.xml")]
)
@com.intellij.openapi.components.Service
class PerplexitySettingsState : PersistentStateComponent<PerplexitySettingsState> {
    var estimatedCostPerRequestUsd: Double = 0.02
    var monthlyLimitUsd: Double = 5.0
    var usedUsdThisMonth: Double = 0.0
    var currentMonth: String = YearMonth.now().toString()
    var isMonthStarted: Boolean = false

    var basePrompt: String = """
Eres un asistente experto en programación, refactorización de código, explicación técnica y buenas prácticas. 
Debes analizar el contenido que te envíe y responder de forma clara, ordenada y profesional. 
Tu labor principal es ayudar a entender código, sugerir mejoras, encontrar fallos y optimizar tanto la lógica como el estilo según el lenguaje detectado.

- Siempre explica brevemente el problema, la solución y los pasos recomendados.
- Si el usuario solicita reescritura, refactoriza el código con comentarios claros y mejora la legibilidad.
- Si detectas bugs o prácticas inseguras, señalalas y corrígelas.
- Si el usuario pide documentación, genera comentarios tipo Javadoc/KDoc/Python docstring pertinentes.
- Usa solo el lenguaje natural y técnico en español salvo que el usuario indique lo contrario.
- Resume en bullets lo más relevante si tu respuesta supera 15 líneas.
- Nunca aportes contenido irrelevante, enlaces externos o información no solicitada.

Ejemplo de formato:
---
**Problema detectado:** descripción breve
**Solución propuesta:** explicación
**Código resultante:**
[fragmento generado/refactorizado]
**Notas relevantes:** puntos clave/limitaciones

Cumple esto en cada respuesta.
""${'"'}.trimIndent()"""


    override fun getState(): PerplexitySettingsState = this
    override fun loadState(state: PerplexitySettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    fun canMakeRequest(): Boolean {
        val nowMonth = YearMonth.now().toString()
        if (nowMonth != currentMonth) {
            currentMonth = nowMonth
            usedUsdThisMonth = 0.0
            isMonthStarted = true
        }
        if (!isMonthStarted) return false
        return usedUsdThisMonth + estimatedCostPerRequestUsd <= monthlyLimitUsd
    }

    fun registerRequest(cost: Double) {
        usedUsdThisMonth += cost
    }

    fun resetUsage() {
        usedUsdThisMonth = 0.0
    }

    companion object {
        fun getInstance(): PerplexitySettingsState =
            com.intellij.openapi.application.ApplicationManager
                .getApplication()
                .getService(PerplexitySettingsState::class.java)
    }
}
