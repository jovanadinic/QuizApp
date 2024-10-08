package com.example.quizapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.quizapplication.R.id.languageSwitcher
import java.util.*

class QuestionsActivity : AppCompatActivity() {

    private var currentQuestionIndex = 0
    private var currentLanguage: String = ""
    private var userId: Int = 0

    private lateinit var questions: List<Question>
    private lateinit var questionText: TextView
    private lateinit var answerButtons: List<Button>
    private lateinit var feedbackImage: ImageView
    private lateinit var closeFeedbackButton: Button
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var backgroundVideoView: VideoView
    private lateinit var dbHelper: QuizDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

        dbHelper = QuizDatabaseHelper(this)
        userId = generateUserId()

        // Get the current language
        currentLanguage = getCurrentLanguage()
        Log.d("QuestionsActivity", "Current Language: $currentLanguage")

        mainLayout = findViewById(R.id.main)
        questionText = findViewById(R.id.question_text)
        feedbackImage = findViewById(R.id.feedback_image)
        closeFeedbackButton = findViewById(R.id.close_feedback_button)

        answerButtons = listOf(
            findViewById(R.id.answer_button_1),
            findViewById(R.id.answer_button_2),
            findViewById(R.id.answer_button_3),
            findViewById(R.id.answer_button_4)
        )

        backgroundVideoView = findViewById(R.id.backgroundVideoView)
        setupVideoBackground()

        questions = getGeneralQuestions(this)
        displayQuestion()

        answerButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                handleAnswer(index)
            }
        }

        closeFeedbackButton.setOnClickListener {
            hideFeedback()
            nextQuestion()
        }

        val languageSwitcher = findViewById<ImageView>(languageSwitcher)
        languageSwitcher.setOnClickListener {
            Log.d("QuestionsActivity", "Language Switcher clicked")
            if (currentLanguage == "en") {
                changeLanguage("de")
            } else {
                changeLanguage("en")
            }
        }
    }

    private fun saveLanguage(language: String) {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("LANGUAGE_KEY", language)
        editor.apply()
        Log.d("QuestionsActivity", "Language Saved: $language")
    }

    private fun getCurrentLanguage(): String {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("LANGUAGE_KEY", Locale.getDefault().language) ?: Locale.getDefault().language
        Log.d("QuestionsActivity", "Retrieved Language: $language")
        return language
    }

    private fun changeLanguage(language: String) {
        Log.d("QuestionsActivity", "Changing language to: $language")
        saveLanguage(language)
        recreate()
    }

    private fun generateUserId(): Int {
        // Retrieve the last used user ID from SharedPreferences
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val lastUserId = sharedPreferences.getInt("LAST_USER_ID", 0)

        // Increment the user ID for the new session
        val newUserId = lastUserId + 1

        // Save the new user ID back to SharedPreferences for the next session
        val editor = sharedPreferences.edit()
        editor.putInt("LAST_USER_ID", newUserId)
        editor.apply()

        // Return the new user ID for this session
        return userId
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

    private fun displayQuestion() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            questionText.text = question.question

            var lastVisibleButton: Button? = null

            answerButtons.forEachIndexed { index, button ->
                if (index < question.options.size) {
                    button.text = question.options[index]
                    button.visibility = View.VISIBLE
                    lastVisibleButton = button
                } else {
                    button.visibility = View.GONE
                }
            }

            lastVisibleButton?.let {
                val constraintSet = ConstraintSet()
                constraintSet.clone(mainLayout)
                constraintSet.connect(R.id.feedback_image, ConstraintSet.TOP, it.id, ConstraintSet.BOTTOM, 20)
                constraintSet.applyTo(mainLayout)
            }
        } else {
            showCompletionScreen()
        }
    }

    private fun handleAnswer(answerIndex: Int) {
        if (currentQuestionIndex < questions.size) {
            // Get the current question
            val currentQuestion = questions[currentQuestionIndex]

            // Get the selected answer based on the answerIndex
            val selectedAnswer = currentQuestion.options[answerIndex]

            // Store the answer in the database
            dbHelper.insertAnswer(userId, currentQuestion.id, selectedAnswer)

            // Show feedback based on the selected answer
            showFeedback(answerIndex)

        } else {
            nextQuestion()
        }
    }

    private fun showFeedback(answerIndex: Int) {
        val feedbackImageName = getFeedbackImageName(currentQuestionIndex + 1, answerIndex)
        Log.d("QuestionsActivity", "Feedback Image Name: $feedbackImageName")
        val resId = resources.getIdentifier(feedbackImageName, "drawable", packageName)

        if (resId != 0) {
            feedbackImage.setImageResource(resId)

            questionText.visibility = View.GONE
            answerButtons.forEach { it.visibility = View.GONE }

            feedbackImage.visibility = View.VISIBLE
            closeFeedbackButton.visibility = View.VISIBLE
        } else {
            Log.e("QuestionsActivity", "Feedback image not found: $feedbackImageName")
            feedbackImage.visibility = View.GONE
        }
    }

    private fun getFeedbackImageName(questionNumber: Int, answerIndex: Int): String {
        val answerChar = ('a' + answerIndex).toString()

        val baseName = "feedback_${questionNumber}_${answerChar}"

        Log.d("QuestionsActivity", "Current Language: $currentLanguage")


        return if (currentLanguage == "en") {
            Log.d("QuestionsActivity", "Loading English image: ${baseName}_en")
            "${baseName}_en"
        } else if (currentLanguage == "de") {
            Log.d("QuestionsActivity", "Loading German image: $baseName")
            baseName
        } else {
            Log.e("QuestionsActivity", "Unknown language, defaulting to base")
            baseName
        }
    }

    private fun hideFeedback() {
        feedbackImage.visibility = View.GONE
        closeFeedbackButton.visibility = View.GONE
        questionText.visibility = View.VISIBLE
        answerButtons.forEach { it.visibility = View.VISIBLE }
    }

    private fun nextQuestion() {
        currentQuestionIndex++
        displayQuestion()
    }

    private fun logSavedAnswers() {
        val allAnswers = dbHelper.getAllAnswers()

        for (answer in allAnswers) {
            Log.d("QuizAnswers", "Question ID: ${answer.questionId}, Selected Answer: ${answer.selectedAnswer}, Timestamp: ${answer.timestamp}")
        }
    }

    private fun showCompletionScreen() {
        questionText.text = getString(R.string.completion_message)
        answerButtons.forEach { it.visibility = View.GONE }
        closeFeedbackButton.visibility = View.GONE
        feedbackImage.visibility = View.GONE

        logSavedAnswers()

        closeFeedbackButton.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}


