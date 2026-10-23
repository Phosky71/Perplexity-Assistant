# AGENTS.md — Perplexity Sonar Pro Assistant

## 🛑 REGLA ABSOLUTA DE SEGURIDAD PRE-COMMIT
Queda terminantemente prohibido ejecutar `git commit` o `git push` sin haber ejecutado previamente `operia-code-gate` (que orquesta `operia-test-destroyer` y `operia-diff-auditor`) sobre los cambios actuales. Si detectas la intención de commitear, intercepta la acción, ejecuta el gate, muestra el semáforo y espera confirmación explícita.

## 💰 PROTOCOLO DE AHORRO DE TOKENS (MODELOS DE PAGO)
Si estás ejecutando sobre un modelo comercial de pago (Claude Sonnet/Opus, GPT-4o, OpenRouter de pago) o el usuario solicita optimización de consumo, activa y cumple estrictamente las directivas de `operia-token-saver`: cero relleno conversacional, lecturas de código quirúrgicas mediante rangos/grep (prohibido volcar archivos >150 líneas) y modificaciones atómicas vía diff.