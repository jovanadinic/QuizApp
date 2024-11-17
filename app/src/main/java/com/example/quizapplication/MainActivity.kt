package com.example.quizapplication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.core.animation.addListener
import java.util.Locale

class MainActivity : ComponentActivity() {

    private var currentTextIndex = 0
    private var isAnimating = true

    private lateinit var animatorSet: AnimatorSet
    private lateinit var animatedTextView: TextView
    private lateinit var videoView: VideoView
    private lateinit var startButton: Button
    private lateinit var robotStart: ImageView
    private lateinit var languageSwitcher: ImageView
    private lateinit var factsArray: Array<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        val languageCode = LanguageManager.language.code
        setLocale(languageCode)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)


        animatedTextView = findViewById(R.id.animatedTextView)
        languageSwitcher = findViewById(R.id.languageSwitcher)
        videoView = findViewById(R.id.backgroundVideoView)
        startButton = findViewById(R.id.startButton)
        robotStart = findViewById(R.id.robotStart)

        factsArray = resources.getStringArray(R.array.facts)

        setupVideoBackground()
        startRobotJumpingAnimation()

        languageSwitcher.setOnClickListener {
            val newLanguageCode = if (languageCode == "de") "en" else "de"
            setLocale(newLanguageCode)

            sharedPreferences.edit().putString("language", newLanguageCode).apply()
            if (LanguageManager.language.code == "de") LanguageManager.language.switchToEnglish() else LanguageManager.language.switchToGerman()

            recreate()
        }

        if (factsArray.isNotEmpty()) {
            startTextAnimation(factsArray)
        }

        startButton.setOnClickListener {
            startStorylineVideo()
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
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
            val intent = Intent(this@MainActivity, QuestionsActivity::class.java)
            startActivity(intent)
            finish()
        }

        videoView.setOnErrorListener{ _, what, extra ->
            Log.e("VideoViewError", "Error occurred: What=$what, Extra=$extra")
            true
        }

        startButton.visibility = View.GONE
        findViewById<View>(R.id.title_text).visibility = View.GONE
        findViewById<View>(R.id.subtitle_text).visibility = View.GONE
        languageSwitcher.visibility = View.GONE
        findViewById<View>(R.id.lock_icon).visibility = View.GONE
        findViewById<View>(R.id.speech_bubble).visibility = View.GONE
        findViewById<View>(R.id.animatedTextView).visibility = View.GONE
        findViewById<View>(R.id.robotStart).visibility = View.GONE

    }

    private fun startTextAnimation(factsArray: Array<String>) {
        if (currentTextIndex >= factsArray.size) {
            currentTextIndex = 0
        }

        val fullText = factsArray[currentTextIndex]
        val sentences = fullText.split(". ")

        if (sentences.size >= 2) {
            val firstSentence = sentences[0] + "."
            val secondSentence = sentences[1]
            animateText(firstSentence, secondSentence)
        } else {
            animateText(fullText, "")
        }
    }

    private fun animateText(firstSentence: String, secondSentence: String) {
        val appearAnimator = ObjectAnimator.ofFloat(animatedTextView, "alpha", 0f, 1f).apply {
            duration = 2000
        }

        val disappearAnimator = ObjectAnimator.ofFloat(animatedTextView, "alpha", 1f, 0f).apply {
            duration = 2000
        }

        animatorSet = AnimatorSet()

        animatorSet.apply {
            playSequentially(
                appearAnimator.clone().apply {
                    addListener(onStart = { animatedTextView.text = firstSentence })
                },
                ObjectAnimator.ofFloat(animatedTextView, "alpha", 1f, 1f).apply {
                    duration = 3000
                },
                disappearAnimator.clone(),
                appearAnimator.clone().apply {
                    addListener(onStart = { animatedTextView.text = secondSentence })
                },
                ObjectAnimator.ofFloat(animatedTextView, "alpha", 1f, 1f).apply {
                    duration = 3000
                },
                disappearAnimator.clone()
            )
            interpolator = AccelerateDecelerateInterpolator()

            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    if (isAnimating) {
                        currentTextIndex++
                        startTextAnimation(factsArray)
                    }
                }
            })
        }

        animatedTextView.visibility = View.VISIBLE
        animatorSet.start()
    }

    private fun startRobotJumpingAnimation() {
        val upAnimator = ObjectAnimator.ofFloat(robotStart, "translationY", 0f, -50f).apply {
            duration = 2000
            interpolator = AccelerateDecelerateInterpolator()
        }

        val downAnimator = ObjectAnimator.ofFloat(robotStart, "translationY", -50f, 0f).apply {
            duration = 2000
            interpolator = AccelerateDecelerateInterpolator()
        }

        val robotAnimatorSet = AnimatorSet().apply {
            playSequentially(upAnimator, downAnimator)
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    start()
                }
            })
        }

        robotAnimatorSet.start()
    }
}