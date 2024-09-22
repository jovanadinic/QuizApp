package com.example.quizapplication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
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

class MainActivity : ComponentActivity() {

    private val texts = arrayOf(
        "Mitarbeiter von Unibw verbringen etwa 80% ihrer Arbeitszeit am Computer. Wie sicher sind deine online Daten?",
        "Jede Person hat durchschnittlich 50 online Konten. Wie merkst du dir so viele Passwörter?",
        "Mehr als 80% von Mitarbeitern von Unibw nutzt gleiche Passwörter für mehrere Kontos. Lass uns das ändern!",
        "123456 ist immer noch das meistbenutzte Passwort weltweit. Wie sicher sind deine Passwörter wirklich?",
        "Passwörter wie 'password' und 'qwerty' sind immer noch weit verbreitet. Hast du ein sicheres Passwort?",
        "Über 80% der Datenlecks passieren wegen schwachen oder gestohlenen Passwörtern. Wie gut schützt du deine online Konten?",
        "Jeder Zweite hat bereits einmal ein Passwort vergessen und musste es zurücksetzen. Gehörst du dazu?",
        "Bei Cyber Attacks dauert es oft nur Sekunden, um ein einfaches Passwort zu knacken. Wie lange würde deins aushalten?",
        "In Deutschland war 2022 jede vierte Person von einem Cyber Attack betroffen. Wie schützt du deine Daten?",
        "Nur 1 von 3 Menschen nutzt derzeit einen Passwort-Manager. Gehörst du bereits dazu?",
        "Jede Sekunde werden Tausende von Passwörtern gehackt. Ist deins sicher genug?",
        "Ein guter Passwort Manager könnte 90% der Sicherheitsrisiken minimieren. Nutzt du bereits einen?"
    )

    private var currentTextIndex = 0
    private var isAnimating = true
    private lateinit var animatorSet: AnimatorSet
    private lateinit var animatedTextView: TextView
    private lateinit var videoView: VideoView
    private lateinit var startButton: Button
    private lateinit var robotStart: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoView = findViewById(R.id.backgroundVideoView)
        animatedTextView = findViewById(R.id.animatedTextView)
        startButton = findViewById(R.id.startButton)
        robotStart = findViewById(R.id.robotStart)

        setupVideoBackground()
        startTextAnimation()
        startRobotJumpingAnimation()

        startButton.setOnClickListener {
            isAnimating = false
            animatorSet.cancel()
            val intent = Intent(this, QuestionsActivity::class.java)
            startActivity(intent)
            finish()
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

        videoView.setOnErrorListener { mediaPlayer, what, extra ->
            Log.e("VideoViewError", "Error occurred: What=$what, Extra=$extra")
            true
        }

        videoView.requestFocus()
    }

    private fun startTextAnimation() {
        if (currentTextIndex >= texts.size) {
            currentTextIndex = 0
        }

        val fullText = texts[currentTextIndex]
        val sentences = fullText.split(". ")

        if (sentences.size >= 2) {
            val firstSentence = sentences[0] + "."
            val secondSentence = sentences[1]
            animateText(firstSentence, secondSentence)
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
                disappearAnimator.clone(),
                ObjectAnimator.ofFloat(animatedTextView, "alpha", 0f, 0f).apply {
                    duration = 1000
                    startDelay = 1000
                }
            )
            interpolator = AccelerateDecelerateInterpolator()

            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    if (isAnimating) {
                        currentTextIndex++
                        startTextAnimation()
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

        val animatorSet = AnimatorSet().apply {
            playSequentially(upAnimator, downAnimator)
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    start()
                }
            })
        }

        animatorSet.start()
    }
}
