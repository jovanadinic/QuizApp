package com.example.quizapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class EndScreenActivity : AppCompatActivity() {
    private lateinit var backgroundVideoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_screen)

        backgroundVideoView = findViewById(R.id.backgroundVideoView)
        setupVideoBackground()

        val totalScore = intent.getIntExtra("TOTAL_SCORE", 0)
        val percentageSafe = intent.getIntExtra("PERCENTAGE_SAFE", 0)

        val totalScoreTextView = findViewById<TextView>(R.id.score_text)
        totalScoreTextView.text = getString(R.string.total_score_text, totalScore)

        val safetyMessageTextView = findViewById<TextView>(R.id.safety_message)
        safetyMessageTextView.text = getString(R.string.safety_message_end, percentageSafe)

        val speechBubbleTextView = findViewById<TextView>(R.id.speech_bubble_text)
        speechBubbleTextView.text = getString(R.string.speech_bubble_message)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 30_000)
    }

    private fun setupVideoBackground() {
        val videoPath = "android.resource://" + packageName + "/" + R.raw.start_background
        val uri: Uri = Uri.parse(videoPath)
        backgroundVideoView.setVideoURI(uri)
        backgroundVideoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            backgroundVideoView.start()
        }
        backgroundVideoView.requestFocus()
    }
}
