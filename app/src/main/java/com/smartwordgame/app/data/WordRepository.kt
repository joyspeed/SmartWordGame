package com.smartwordgame.app.data

import android.content.Context
import org.json.JSONArray

object WordRepository {
    fun loadWords(context: Context): List<WordItem> {
        val json = context.assets.open("words.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
        val jsonArray = JSONArray(json)

        return List(jsonArray.length()) { index ->
            val item = jsonArray.getJSONObject(index)
            WordItem(
                id = item.getInt("id"),
                word = item.getString("word"),
                explanation = item.getString("explanation")
            )
        }
    }
}
