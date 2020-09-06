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
        var speakButtonBGI = true

        submitButton.setOnClickListener {
            if (journalText.text.isNotEmpty()) {
                val data = journalText.text.toString()
                journalText.text.clear()
            }
        }

        speakButton.setOnClickListener {
            speakButtonBGI = if (speakButtonBGI) {
                speakButton.setBackgroundResource(R.drawable.stop)
                false
            } else {
                speakButton.setBackgroundResource(R.drawable.start)
                true
            }
        }

        logButton.setOnClickListener {
            setContentView(R.layout.logs)
        }
    }
}

class DataLogs() {

}

