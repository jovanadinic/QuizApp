package com.example.quizapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File

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

        private const val TAG = "QuizDatabaseHelper"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = ("CREATE TABLE $TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_USER_ID INTEGER, "
                + "$COLUMN_QUESTION_ID INTEGER, "
                + "$COLUMN_SELECTED_ANSWER TEXT, "
                + "$COLUMN_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP)")
        db.execSQL(CREATE_TABLE)
        Log.d(TAG, "Database table created: $TABLE_NAME")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
        Log.d(TAG, "Database upgraded from version $oldVersion to $newVersion")
    }

    fun insertAnswer(userId: Int, questionId: Int, selectedAnswer: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COLUMN_USER_ID, userId)
        contentValues.put(COLUMN_QUESTION_ID, questionId)
        contentValues.put(COLUMN_SELECTED_ANSWER, selectedAnswer)

        val success = db.insert(TABLE_NAME, null, contentValues)

        Log.d(TAG, "Inserted Answer: userId=$userId, questionId=$questionId, selectedAnswer=$selectedAnswer, success=$success")

        db.close()
        return success
    }

    fun getLastUserId(): Int {
        val db = this.readableDatabase
        val query = "SELECT MAX($COLUMN_USER_ID) FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        var lastUserId = 0
        if (cursor.moveToFirst()) {
            lastUserId = cursor.getInt(0)
        }

        cursor.close()
        db.close()

        Log.d(TAG, "Last User ID Retrieved: $lastUserId")
        return lastUserId
    }

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

        Log.d(TAG, "Fetched ${answersList.size} answers from the database")
        return answersList
    }

    fun exportToCSV(context: Context): String {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        val file = File(context.getExternalFilesDir(null), "quiz_answers.csv")

        file.printWriter().use { writer ->

            writer.println("id,user_id,question_id,selected_answer,timestamp")

            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
                val questionId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID))
                val selectedAnswer = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SELECTED_ANSWER))
                val timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))

                writer.println("$id,$userId,$questionId,$selectedAnswer,$timestamp")
            }
        }

        cursor.close()
        db.close()

        Log.d(TAG, "Exported data to file: ${file.absolutePath}")

        return file.absolutePath
    }

}

data class Answer(val id: Int, val userId: Int, val questionId: Int, val selectedAnswer: String, val timestamp: String)
