package com.example.journalentry.database

data class Sentence(val noteId: Int, val content: String, val sentiment: Float, val updated: Int, val intent: String, val confidence: Float) {
}