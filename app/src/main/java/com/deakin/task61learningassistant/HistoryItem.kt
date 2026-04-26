package com.deakin.task61learningassistant

data class HistoryItem(
    val prompt: String,
    val response: String,
    val featureType: String,
    val createdAt: Long = System.currentTimeMillis()
)
