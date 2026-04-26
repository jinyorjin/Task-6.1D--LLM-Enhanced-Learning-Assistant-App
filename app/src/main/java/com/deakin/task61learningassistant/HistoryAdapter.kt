package com.deakin.task61learningassistant

import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter {

    private val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    fun render(
        container: LinearLayout,
        items: List<HistoryItem>,
        onShareClicked: (HistoryItem) -> Unit
    ) {
        container.removeAllViews()
        val inflater = LayoutInflater.from(container.context)

        items.forEach { item ->
            val itemView = inflater.inflate(R.layout.item_history, container, false)
            bind(itemView, item, onShareClicked)
            container.addView(itemView)
        }
    }

    private fun bind(view: View, item: HistoryItem, onShareClicked: (HistoryItem) -> Unit) {
        view.findViewById<TextView>(R.id.tvFeatureType).text = "Feature: ${item.featureType}"
        view.findViewById<TextView>(R.id.tvPrompt).text = "Prompt: ${item.prompt}"
        view.findViewById<TextView>(R.id.tvResponse).text = "AI Response: ${item.response}"
        view.findViewById<TextView>(R.id.tvDateTime).text = formatter.format(Date(item.createdAt))
        view.findViewById<Button>(R.id.btnShareItem).setOnClickListener { onShareClicked(item) }
    }
}
