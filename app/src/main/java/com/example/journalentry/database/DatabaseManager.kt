package com.example.journalentry.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.journalentry.MenuActivity

class DatabaseManager(context: AppCompatActivity): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VER) {
    companion object {
        private const val DATABASE_VER = 1
        private const val DATABASE_NAME = "name.db"

        // Table 1
        private const val TABLE1_NAME = "Entity"
        private const val COL1_KEY = "Key"
        private const val COL1_VALUE = "Value"
        private const val COL1_ID = "Id"
        private const val COL1_SENTENCE_ID = "SentenceId"
        private const val COL1_TYPE = "Type"
        private const val COL1_SCORE = "Score"

        // Table 2
        private const val TABLE2_NAME = "Note"
        private const val COL2_ID = "Id"
        private const val COL2_CONTENT = "Content"
        private const val COL2_INTENT = "Intent"
        private const val COL2_DATETIME = "DateTime"

        // Table 3
        private const val TABLE3_NAME = "Sentence"
        private const val COL3_ID = "Id"
        private const val COL3_NOTE_ID = "NoteId"
        private const val COL3_CONTENT = "Content"
        private const val COL3_SENTIMENT = "Sentiment"
        private const val COL3_ENTITIES_ID = "EntitiesId"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE1_QUERY: String = "CREATE TABLE $TABLE1_NAME ($COL1_ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + COL1_KEY + "TEXT, $COL1_VALUE TEXT NOT NULL, $COL1_SENTENCE_ID INTEGER NOT NULL, $COL1_TYPE TEXT NOT NULL, $COL1_SCORE INTEGER NOT NULL)"
        val CREATE_TABLE2_QUERY: String = "CREATE TABLE $TABLE2_NAME ($COL2_ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, $COL2_CONTENT TEXT NOT NULL, $COL2_INTENT TEXT, $COL2_DATETIME TEXT NOT NULL)"
        val CREATE_TABLE3_QUERY: String = "CREATE TABLE $TABLE3_NAME ($COL3_ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, $COL3_NOTE_ID INTEGER NOT NULL, $COL3_CONTENT TEXT NOT NULL, $COL3_SENTIMENT INTEGER, $COL3_ENTITIES_ID INTEGER)"

        db!!.execSQL(CREATE_TABLE1_QUERY)
        db.execSQL(CREATE_TABLE2_QUERY)
        db.execSQL(CREATE_TABLE3_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE1_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE2_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE3_NAME")
    }

    fun addEntities(entity: Entity) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL1_KEY, entity.key)
        values.put(COL1_VALUE, entity.value)
        values.put(COL1_SENTENCE_ID, entity.sentenceId)
        values.put(COL1_TYPE, entity.type)
        values.put(COL1_SCORE, entity.score)

        db.insert(TABLE1_NAME, null, values)
        db.close()
    }

    fun addSentence(sentence: Sentence) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL3_NOTE_ID, sentence.noteId)
        values.put(COL3_CONTENT, sentence.content)
        values.put(COL3_SENTIMENT, sentence.sentiment)
        values.put(COL3_ENTITIES_ID, sentence.entitiesId)

        db.insert(TABLE3_NAME, null, values)
        db.close()
    }

    fun updateSentence(sentence: Sentence, id: Int) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL3_NOTE_ID, sentence.noteId)
        values.put(COL3_CONTENT, sentence.content)
        values.put(COL3_SENTIMENT, sentence.sentiment)
        values.put(COL3_ENTITIES_ID, sentence.entitiesId)
        db.update(TABLE3_NAME, values, "$COL3_ID = $id", null)
        db.close()
    }

    fun getSentenceId(sentence: String): Int {
        val db = this.readableDatabase
        val query = "SELECT $COL3_ID FROM $TABLE3_NAME WHERE $COL3_CONTENT=?"
        val cursor = db.rawQuery(query, arrayOf(sentence))
        var id = 0
        if (cursor != null) {
            cursor.moveToFirst()
            id = cursor.getString(cursor.getColumnIndex(COL3_ID)).toInt()
        }
        cursor.close()
        return id
    }

    fun getSentence(sentence: String): Sentence {
        val db = this.readableDatabase
        val query = "SELECT $COL3_ID FROM $TABLE3_NAME WHERE $COL3_CONTENT=?"
        val cursor = db.rawQuery(query, arrayOf(sentence))
        var enti = 0
        var senti = 0
        var noteId = 0
        if (cursor != null) {
            cursor.moveToFirst()
            noteId = cursor.getString(cursor.getColumnIndex(COL3_NOTE_ID)).toInt()
            enti = cursor.getString(cursor.getColumnIndex(COL3_ENTITIES_ID)).toInt()
            senti = cursor.getString(cursor.getColumnIndex(COL3_SENTIMENT)).toInt()
        }
        cursor.close()
        return Sentence(noteId, sentence,   senti, enti)
    }

    fun getSentences(): List<Sentence> {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE3_NAME"
        val cursor = db.rawQuery(query,null)
        val sentArr = List(cursor.count) { Sentence(0,"",0,0) }.toMutableList()
        var j = 0
        while(cursor.moveToNext()) {
            val tempSentence = Sentence(cursor.getString(1).toInt(), cursor.getString(2), cursor.getString(3).toInt(), cursor.getString(4).toInt())
            sentArr[j] = tempSentence
            j++
        }
        db.close()
        return sentArr
    }

    fun addNote(note: Note) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL2_CONTENT, note.content)
        values.put(COL2_INTENT, note.intent)
        values.put(COL2_DATETIME, note.dateTime)

        db.insert(TABLE2_NAME, null, values)
        db.close()
    }

    fun getNotes(): List<Note> {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE2_NAME"
        val cursor = db.rawQuery(query,null)
        val noteArr = List(cursor.count) { Note("","","") }.toMutableList()
        var j = 0
        while(cursor.moveToNext()) {
            val tempNote = Note(cursor.getString(1), cursor.getString(2), cursor.getString(3))
            noteArr[j] = tempNote
            j++
        }
        return noteArr
    }

    fun getNoteId(note:Note): Int {
        val db = this.readableDatabase
        val query = "SELECT $COL2_ID FROM $TABLE2_NAME WHERE $COL2_CONTENT=?"
        val cursor = db.rawQuery(query, arrayOf(note.content))
        var id = 0
        if (cursor != null) {
            cursor.moveToFirst()
            id = cursor.getString(cursor.getColumnIndex(COL2_ID)).toInt()
        }
        cursor.close()
        db.close()
        return id
    }
}