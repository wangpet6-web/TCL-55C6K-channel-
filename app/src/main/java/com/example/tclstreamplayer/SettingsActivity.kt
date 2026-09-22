package com.example.tclstreamplayer

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val input = findViewById<EditText>(R.id.playlistUrlInput)
        val save = findViewById<Button>(R.id.saveButton)

        input.setText(Prefs.getPlaylistUrl(this) ?: "")

        save.setOnClickListener {
            val url = input.text.toString().trim()
            if (url.isBlank() || !(url.startsWith("http://") || url.startsWith("https://"))) {
                Toast.makeText(this, "Enter a valid http/https M3U URL", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            Prefs.setPlaylistUrl(this, url)
            Toast.makeText(this, "Saved. Reloading channels…", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
