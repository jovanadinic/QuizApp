package com.example.quizapplication

import android.content.Context

data class Question(
    val id: Int,
    val question: String,
    val options: List<String>,
    val feedbackCount: Int,
    val points: List<Int>
)

fun getGeneralQuestions(context: Context): List<Question> {
    return listOf(
        Question(
            id = 1,
            question = context.getString(R.string.question_1),
            options = context.resources.getStringArray(R.array.options_question_1).toList(),
            feedbackCount = 3,
            points = listOf(5, 10, 15)
        ),
        Question(
            id = 2,
            question = context.getString(R.string.question_2),
            options = context.resources.getStringArray(R.array.options_question_2).toList(),
            feedbackCount = 3,
            points = listOf(5, 15, 10)
        ),
        Question(
            id = 3,
            question = context.getString(R.string.question_3),
            options = context.resources.getStringArray(R.array.options_question_3).toList(),
            feedbackCount = 2,
            points = listOf(5, 15)
        ),
        Question(
            id = 4,
            question = context.getString(R.string.question_4),
            options = context.resources.getStringArray(R.array.options_question_4).toList(),
            feedbackCount = 4,
            points = listOf(5, 5, 15, 10)
        ),
        Question(
            id = 5,
            question = context.getString(R.string.question_5),
            options = context.resources.getStringArray(R.array.options_question_5).toList(),
            feedbackCount = 4,
            points = listOf(10, 10, 10, 15)
        ),
        Question(
            id = 6,
            question = context.getString(R.string.question_6),
            options = context.resources.getStringArray(R.array.options_question_6).toList(),
            feedbackCount = 2,
            points = listOf(15, 5)
        )
    )
}


