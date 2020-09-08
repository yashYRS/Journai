package com.example.journalentry.database

data class Sentence(val noteId: Int, val content: String, var sentiment: Int, val entitiesId: Int) {
}