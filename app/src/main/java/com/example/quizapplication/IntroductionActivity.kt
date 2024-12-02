package com.example.quizapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class IntroductionActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)

        videoView = findViewById(R.id.backgroundVideoView)

        setupVideoBackground()

        val startQuizButton: Button = findViewById(R.id.startQuizButton)
        startQuizButton.backgroundTintList = null
        startQuizButton.setOnClickListener {
            startStorylineVideo()
        }
    }

    private fun setupVideoBackground() {
        val videoPath = "android.resource://" + packageName + "/" + R.raw.start_background
        val uri: Uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)

        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            videoView.start()
        }

        videoView.setOnErrorListener { _, what, extra ->
            Log.e("VideoViewError", "Error occurred: What=$what, Extra=$extra")
            true
        }

        videoView.requestFocus()
    }

    private fun startStorylineVideo() {
        videoView.stopPlayback()
        findViewById<View>(R.id.introduction_text).visibility = View.GONE
        findViewById<View>(R.id.startQuizButton).visibility = View.GONE
        findViewById<View>(R.id.robot_image).visibility = View.GONE

        val videoPath = if (LanguageManager.language.code == "de") {
            "android.resource://${packageName}/" + R.raw.storyline_de
        } else {
            "android.resource://${packageName}/" + R.raw.storyline_en
        }

        val uri: Uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)

        videoView.setOnPreparedListener{ mediaPlayer ->
            mediaPlayer.isLooping = false
            videoView.start()
        }

        videoView.setOnCompletionListener {
            val intent = Intent(this@IntroductionActivity, QuestionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        videoView.setOnErrorListener{ _, what, extra ->
            Log.e("VideoViewError", "Error occurred: What=$what, Extra=$extra")
            true
        }
    }

}
