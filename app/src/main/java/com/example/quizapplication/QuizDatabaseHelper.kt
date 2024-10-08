package com.example.quizapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class QuizDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "quiz_answers.db"
        private const val DATABASE_VERSION = 2

        private const val TABLE_NAME = "answers"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_QUESTION_ID = "question_id"
        private const val COLUMN_SELECTED_ANSWER = "selected_answer"
        private const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = ("CREATE TABLE $TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_USER_ID TEXT, "
                + "$COLUMN_QUESTION_ID INTEGER, "
                + "$COLUMN_SELECTED_ANSWER TEXT, "
                + "$COLUMN_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP)")
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertAnswer(userId: Int, questionId: Int, selectedAnswer: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        // Add data to content values
        contentValues.put(COLUMN_USER_ID, userId)
        contentValues.put(COLUMN_QUESTION_ID, questionId)
        contentValues.put(COLUMN_SELECTED_ANSWER, selectedAnswer)

        // Insert a new row into the database
        val success = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return success
    }

    // Method to retrieve all stored answers
    fun getAllAnswers(): List<Answer> {
        val answersList: MutableList<Answer> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
                val questionId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID))
                val selectedAnswer = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SELECTED_ANSWER))
                val timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))

                val answer = Answer(id, userId, questionId, selectedAnswer, timestamp)
                answersList.add(answer)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return answersList
    }
}

// Data class to represent an answer
data class Answer(val id: Int, val userId: Int, val questionId: Int, val selectedAnswer: String, val timestamp: String)
