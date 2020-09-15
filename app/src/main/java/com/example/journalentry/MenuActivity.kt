package com.example.journalentry

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.journalentry.database.DatabaseManager
import com.example.journalentry.database.Note
import com.example.journalentry.database.Sentence
import com.example.journalentry.getapi.ApiManager
import java.net.URLEncoder.encode
import java.text.SimpleDateFormat
import java.util.*

class MenuActivity: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.home, container, false)
        val dP = DataLogs()
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(view.context)
        val submitButton: Button = view.findViewById(R.id.submit_button)
        val journalText: EditText = view.findViewById(R.id.journal_entry)
        val speakButton: Button = view.findViewById(R.id.speak_button)
        var startSpeech = false
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        fun parseData(data: String) {
            val db = DatabaseManager(activity as AppCompatActivity)
            val note = Note(data, "", dateFormat.format(Date()))
            db.addNote(note)
            val noteId = db.getNoteId(note)
            val sentences = dP.dataSplitter(data)
            for (s in sentences) {
                val sentence = Sentence(noteId, s, 0f, 0, "", 0f)
                db.addSentence(sentence)
            }
            db.close()
        }

        fun submitEventCallBacK(query: String) {
            // Request a string response from the provided URL.
            ApiManager().getRequest(query, activity as AppCompatActivity)
        }

        fun updateNotes(note: Note) {
            val db = DatabaseManager(activity as AppCompatActivity)
            val noteId = db.getNoteId(note)
            val sentenceNotes = db.getSentencesForNote(noteId)
            var maxConf = 0f
            var i = 0
            var index = i
            for (s in sentenceNotes) {
                if (s.confidence > maxConf) {
                    maxConf = s.confidence
                    index = i
                }
                i += 1
            }
            val netIntent = sentenceNotes[index].intent
            val newNote = Note(note.content, netIntent, note.dateTime)
            db.updateNote(newNote, noteId)
            db.close()
        }

        fun updateDB() {
            val db = DatabaseManager(activity as AppCompatActivity)
            val strings = db.getSentences()
            val notes = db.getNotes()
            db.close()
            for (s in strings) {
                if (s.updated == 0) {
                    submitEventCallBacK(s.content)
                }
            }
            for (n in notes) {
                updateNotes(n)
            }
        }

        submitButton.setOnClickListener {
            if (journalText.text.isNotEmpty()) {
                val data = journalText.text.toString()
                parseData(data)
                updateDB()
                journalText.text.clear()
            }
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}
            override fun onBeginningOfSpeech() {
                journalText.setText("Listening...")
            }

            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray) {}
            override fun onEndOfSpeech() {
                journalText.text.clear()
                speakButton.setBackgroundResource(R.drawable.start)
                startSpeech = false
            }
            override fun onError(i: Int) {
                journalText.text.clear()
                speakButton.setBackgroundResource(R.drawable.start)
                startSpeech = false
            }

            override fun onResults(bundle: Bundle) {
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                journalText.setText(data!![0])
                speakButton.setBackgroundResource(R.drawable.start)
                startSpeech = false
            }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle) {}
        })

        speakButton.setOnClickListener {
            startSpeech = if (startSpeech) {
                speakButton.setBackgroundResource(R.drawable.start)
                speechRecognizer.stopListening()
                false
            } else {
                speakButton.setBackgroundResource(R.drawable.stop)
                speechRecognizer.startListening(speechRecognizerIntent)
                true
            }
        }
        return view
    }
}

class DataLogs() {
    private fun textParser(data: String): String {
        return encode(data, "UTF-8").replace("+", "%20")
    }
    fun dataSplitter(text: String): ArrayList<String> {
        val output = arrayListOf<String>()
        var counter = 1
        var str = ""
        for (i in text.split(" ")) {
            counter += i.length + 1
            if (counter > 260) {
                str.trim()
                output.add(textParser(str))
                str = ""
                counter = 1
            }
            str += " $i"
        }
        str.trim()
        output.add(textParser(str))
        return output
    }
}