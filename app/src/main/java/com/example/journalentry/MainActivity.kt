package com.example.journalentry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val journalText: EditText = findViewById(R.id.journal_entry)
        val submitButton: Button = findViewById(R.id.submit_button)
        val logButton: Button = findViewById(R.id.log_button)
        val speakButton: Button = findViewById(R.id.speak_button)

        submitButton.setOnClickListener {
            if (journalText.text.isNotEmpty()) {
                val data = journalText.text.toString()
                journalText.text.clear()
            }
        }

        speakButton.setOnClickListener {
            if (speakButton.text == R.string.mic_button.toString()) {
                speakButton.text = "STOP"
//                speakButton.color
            } else {
                speakButton.text = "SPEAK"
            }
        }

        logButton.setOnClickListener {
            setContentView(R.layout.logs)
        }
    }
}

class DataLogs() {

}

