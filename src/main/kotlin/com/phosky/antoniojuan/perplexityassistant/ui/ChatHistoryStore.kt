package com.phosky.antoniojuan.perplexityassistant.ui

import com.intellij.openapi.application.PathManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object ChatHistoryStore {
    private fun file(): File {
        val dir = File(PathManager.getSystemPath(), "perplexity-assistant")
        dir.mkdirs()
        return File(dir, "history.json")
    }

    fun load(): List<Pair<String, String>> {
        val f = file()
        if (!f.exists()) return emptyList()
        return try {
            val arr = JSONArray(f.readText())
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                o.optString("prompt") to o.optString("response")
            }
        } catch (t: Throwable) {
            emptyList()
        }
    }

    fun save(messages: List<Pair<String, String>>) {
        val arr = JSONArray()
        messages.takeLast(50).forEach { (p, r) ->
            arr.put(JSONObject().put("prompt", p).put("response", r))
        }
        file().writeText(arr.toString())
    }

    fun clear() {
        file().delete()
    }
}