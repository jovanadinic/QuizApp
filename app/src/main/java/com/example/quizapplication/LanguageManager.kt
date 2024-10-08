package com.example.quizapplication

object LanguageManager {
    var language: Language = Language()
}

data class Language(var code: String = "de") {

    fun switchToEnglish() {
        code = "en"
    }

    fun switchToGerman() {
        code = "de"
    }
}
