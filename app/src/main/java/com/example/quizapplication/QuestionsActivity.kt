package com.example.quizapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.quizapplication.R.id.close_feedback_button


class QuestionsActivity : AppCompatActivity() {

    private var currentQuestionIndex = 0
    private var usesPasswordManager = false
    private lateinit var questions: List<Question>
    private lateinit var questionText: TextView
    private lateinit var answerButtons: List<Button>
    private lateinit var feedbackImage: ImageView
    private lateinit var closeFeedbackButton: Button
    private lateinit var mainLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

        mainLayout = findViewById(R.id.main)
        questionText = findViewById(R.id.question_text)
        feedbackImage = findViewById(R.id.feedback_image)
        closeFeedbackButton = findViewById(close_feedback_button)

        answerButtons = listOf(
            findViewById<Button>(R.id.answer_button_1),
            findViewById<Button>(R.id.answer_button_2),
            findViewById<Button>(R.id.answer_button_3),
            findViewById<Button>(R.id.answer_button_4)
        )

        questions = generalQuestions

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
        val question = questions[currentQuestionIndex]

        if (currentQuestionIndex < generalQuestions.size) {
            showFeedback(answerIndex)
        } else {
            nextQuestion()
        }

        if (question.id == 6) {
            usesPasswordManager = answerIndex == 0
            questions = generalQuestions + if (usesPasswordManager) {
                passwordManagerUserQuestions
            } else {
                nonPasswordManagerUserQuestions
            }
        }
    }

    private fun showFeedback(answerIndex: Int) {
        val question = questions[currentQuestionIndex]
        if (question.feedbacks.isNotEmpty()) {

            feedbackImage.setImageResource(question.feedbacks[answerIndex])

            questionText.visibility = View.GONE
            answerButtons.forEach { it.visibility = View.GONE }

            feedbackImage.visibility = View.VISIBLE
            closeFeedbackButton.visibility = View.VISIBLE
        }
    }

    private fun hideFeedback() {
        feedbackImage.visibility = View.GONE
        closeFeedbackButton.visibility = View.GONE
        questionText.visibility = View.VISIBLE
        answerButtons.forEach { it.visibility = View.VISIBLE }
        mainLayout.foreground = null
    }

    private fun nextQuestion() {
        currentQuestionIndex++
        displayQuestion()
    }

    private fun showCompletionScreen() {
        questionText.text = "Danke für deine Teilnahme"
        answerButtons.forEach { it.visibility = View.GONE }
        closeFeedbackButton.visibility = View.GONE
        feedbackImage.visibility = View.GONE

        closeFeedbackButton.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}
