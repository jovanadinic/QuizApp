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
import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources


class QuestionsActivity : AppCompatActivity() {

    private var currentQuestionIndex = 0
    private var currentLanguage: String = ""
    private var userId: Int = 0
    private var totalScore = 0
    private val maxScore = 90

    private lateinit var questions: List<Question>
    private lateinit var questionText: TextView
    private lateinit var answerButtons: List<Button>
    private lateinit var feedbackVideoView: VideoView
    private lateinit var closeFeedbackButton: Button
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var backgroundVideoView: VideoView
    private lateinit var dbHelper: QuizDatabaseHelper
    private lateinit var pageIndicatorLayout: LinearLayout
    private lateinit var pointsTextViews: List<TextView>
    private lateinit var scoreTextView: TextView
    private val pageIndicatorCircles = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

        dbHelper = QuizDatabaseHelper(this)
        userId = generateUserId()

        currentLanguage = LanguageManager.language.code

        mainLayout = findViewById(R.id.main)
        questionText = findViewById(R.id.question_text)
        feedbackVideoView = findViewById(R.id.feedback_video)
        closeFeedbackButton = findViewById(R.id.close_feedback_button)
        pageIndicatorLayout = findViewById(R.id.page_indicator)
        scoreTextView = findViewById(R.id.score_text_view)


        answerButtons = listOf(
            findViewById(R.id.answer_button_1),
            findViewById(R.id.answer_button_2),
            findViewById(R.id.answer_button_3),
            findViewById(R.id.answer_button_4)
        )

        answerButtons.forEach { button ->
            button.backgroundTintList = null
        }

        pointsTextViews = listOf(
            findViewById(R.id.points_text_1),
            findViewById(R.id.points_text_2),
            findViewById(R.id.points_text_3),
            findViewById(R.id.points_text_4)
        )


        backgroundVideoView = findViewById(R.id.backgroundVideoView)
        setupVideoBackground()

        questions = getGeneralQuestions(this)

        setupPageIndicator(questions.size)

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

        closeFeedbackButton.background = AppCompatResources.getDrawable(this, R.drawable.rounded_button_blue)
        closeFeedbackButton.backgroundTintList = null

        val closeButton = findViewById<ImageView>(R.id.close_button)
        closeButton.setOnClickListener {
            // Stop the quiz and go back to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // End the current activity
        }
    }


    private fun generateUserId(): Int {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val lastUserId = sharedPreferences.getInt("LAST_USER_ID", 0)
        val newUserId = lastUserId + 1
        sharedPreferences.edit().putInt("LAST_USER_ID", newUserId).apply()
        return newUserId
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

    private fun setupPageIndicator(totalQuestions: Int) {
        pageIndicatorLayout.removeAllViews() // Clear any existing indicators
        pageIndicatorCircles.clear()

        for (i in 0 until totalQuestions) {
            val circle = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(24, 24).apply {
                    setMargins(8, 0, 8, 0)
                }
                background = AppCompatResources.getDrawable(this@QuestionsActivity, R.drawable.circle_indicator_inactive)
            }
            pageIndicatorLayout.addView(circle)
            pageIndicatorCircles.add(circle)
        }

        updatePageIndicator(0)
    }


    private fun updatePageIndicator(activeIndex: Int) {
        pageIndicatorCircles.forEachIndexed { index, view ->
            val layoutParams = view.layoutParams as LinearLayout.LayoutParams
            if (index == activeIndex) {
                // Apply the active circle drawable and larger size
                layoutParams.width = 15.dpToPx(this)
                layoutParams.height = 15.dpToPx(this)
                view.background = AppCompatResources.getDrawable(this, R.drawable.circle_indicator_active)
            } else {
                // Apply the inactive circle drawable and smaller size
                layoutParams.width = 12.dpToPx(this)
                layoutParams.height = 12.dpToPx(this)
                view.background = AppCompatResources.getDrawable(this, R.drawable.circle_indicator_inactive)
            }
            view.layoutParams = layoutParams // Apply the updated layout params
        }
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    private fun displayQuestion() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            questionText.text = question.question

            pageIndicatorLayout.visibility = View.VISIBLE

            updatePageIndicator(currentQuestionIndex)

            val maxWidthPx = (resources.displayMetrics.widthPixels * 0.8).toInt()

            val longestOptionWidth = question.options.maxOf { option ->
                val tempTextView = TextView(this).apply {
                    text = option
                    textSize = answerButtons.first().textSize
                    typeface = answerButtons.first().typeface
                }
                tempTextView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                tempTextView.measuredWidth
            }.coerceAtMost(maxWidthPx)

            answerButtons.forEachIndexed { index, button ->
                if (index < question.options.size) {
                    button.text = question.options[index]
                    button.visibility = View.VISIBLE
                    button.layoutParams = button.layoutParams.apply { width = longestOptionWidth }
                    button.isSingleLine = false
                    button.maxLines = 3
                    button.ellipsize = null
                    button.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#6D9FD2"))
                    button.isClickable = true
                } else {
                    button.visibility = View.GONE
                }
            }
            pointsTextViews.forEach { it.text = ""; it.visibility = View.GONE }
        } else {
            showCompletionScreen()
        }
    }

    private fun handleAnswer(answerIndex: Int) {
        if (currentQuestionIndex < questions.size) {
            answerButtons.forEach { it.isClickable = false }

            val currentQuestion = questions[currentQuestionIndex]

            totalScore += currentQuestion.points[answerIndex]
            updateScoreDisplay()

            val selectedAnswer = currentQuestion.options[answerIndex]
            dbHelper.insertAnswer(userId, currentQuestion.id, selectedAnswer)

            pointsTextViews[answerIndex].text = "+${currentQuestion.points[answerIndex]}"
            pointsTextViews.forEachIndexed { index, textView ->
                textView.visibility = if (index == answerIndex) View.VISIBLE else View.GONE
            }

            val selectedButton = answerButtons[answerIndex]
            val buttonColor = currentQuestion.buttonColors[answerIndex]

            val color = Color.parseColor(buttonColor)
            selectedButton.backgroundTintList = ColorStateList.valueOf(color)

            selectedButton.postDelayed({
                showFeedback(answerIndex)
            }, 2000)
        }
    }

    private fun updateScoreDisplay() {
        scoreTextView.text = getString(R.string.score_text, totalScore)
    }

    private fun showFeedback(answerIndex: Int) {
        pointsTextViews.forEach { it.visibility = View.GONE }

        val feedbackVideoName = getFeedbackVideoName(currentQuestionIndex + 1, answerIndex)
        val resId = resources.getIdentifier(feedbackVideoName, "raw", packageName)
        if (resId != 0) {
            val videoUri = Uri.parse("android.resource://$packageName/$resId")

            feedbackVideoView.setVideoURI(videoUri)
            feedbackVideoView.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
                feedbackVideoView.start()
            }

            closeFeedbackButton.visibility = View.VISIBLE
            closeFeedbackButton.background = AppCompatResources.getDrawable(this, R.drawable.rounded_button_blue)
            closeFeedbackButton.backgroundTintList = null

            feedbackVideoView.visibility = View.VISIBLE
            questionText.visibility = View.GONE
            answerButtons.forEach { it.visibility = View.GONE }
        } else {
            Log.e("QuestionsActivity", "Video not found: $feedbackVideoName")
            closeFeedbackButton.visibility = View.VISIBLE
        }
    }

    private fun getFeedbackVideoName(questionNumber: Int, answerIndex: Int): String {
        val answerChar = ('a' + answerIndex).toString()
        val baseName = "feedback_${questionNumber}_${answerChar}"
        return if (currentLanguage == "en") "${baseName}_en" else baseName
    }

    private fun hideFeedback() {
        if (feedbackVideoView.isPlaying) {
            feedbackVideoView.stopPlayback()
        }

        feedbackVideoView.visibility = View.GONE
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
        val intent = Intent(this, EndScreenActivity::class.java)
        intent.putExtra("TOTAL_SCORE", totalScore)
        intent.putExtra("PERCENTAGE_SAFE", (totalScore.toFloat() / maxScore * 100).toInt())
        startActivity(intent)
        finish()
    }
}