package com.deakin.task61learningassistant

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class HistoryStorage(private val context: Context) {

    private val prefsName = "history_prefs"

    fun saveHistory(item: HistoryItem) {
        val current = getAllHistory().toMutableList()
        current.add(0, item)
        writeHistory(current)
    }

    fun getAllHistory(): List<HistoryItem> {
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val raw = prefs.getString(getHistoryKey(), "[]") ?: "[]"
        val array = JSONArray(raw)
        val items = mutableListOf<HistoryItem>()
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            items.add(
                HistoryItem(
                    prompt = obj.optString("prompt"),
                    response = obj.optString("response"),
                    featureType = obj.optString("featureType"),
                    createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                )
            )
        }
        return items
    }

    fun getLatestHistory(): HistoryItem? = getAllHistory().firstOrNull()

    private fun writeHistory(items: List<HistoryItem>) {
        val array = JSONArray()
        for (item in items) {
            val obj = JSONObject().apply {
                put("prompt", item.prompt)
                put("response", item.response)
                put("featureType", item.featureType)
                put("createdAt", item.createdAt)
            }
            array.put(obj)
        }
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
            .edit()
            .putString(getHistoryKey(), array.toString())
            .apply()
    }

    private fun getHistoryKey(): String {
        val username = context
            .getSharedPreferences("user_profile", Context.MODE_PRIVATE)
            .getString("username", "")
            ?.trim()
            .orEmpty()
        return "history_items_$username"
    }
}
