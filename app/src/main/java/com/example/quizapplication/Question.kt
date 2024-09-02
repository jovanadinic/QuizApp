package com.example.quizapplication

data class Question (
    val id: Int,
    val question: String,
    val image: Int?,
    val options: List<String>,
    val feedbacks: List<Int>
)


val generalQuestions = listOf(
    Question(id = 1, question = "Wie erstellst du deine Passwörter am häufigsten?", image = null, options = listOf("Ich mache es selbst", "Mein Browser erstellt sie", "Mit einem Passwort Manager"), feedbacks = listOf()),
    Question(id = 2, question = "Verwendest du gleiche Passworter für verschiedene Kontos?", image = null, options = listOf("Ja", "Nein"), feedbacks = listOf()),
    Question(id = 3, question = "Wie speicherst du deine Passwörter?", image = null, options =  listOf("Ich merke sie mir", "Ich notiere sie", "Im Passwort Manager", "Im Browser"), feedbacks = listOf()),
    Question(id = 4, question = "Ein Passwort Manager ist...", image = null, options = listOf("eine Software, die Passwörter verschlüsselt speichert", "eine Software, die sichere Passwörter kreiert", "eine Software, die deine Passwörter automatisch ausfüllt", "eine Software, die alle drei oben benannten Funktionen erfüllt"), feedbacks = listOf()),
    Question(id = 5, question = "Nutzt du einen Passwort Manager?", image = null, options = listOf("Ja", "Nein"), feedbacks = listOf())
)

val passwordManagerUserQuestions = listOf(
    Question(id = 6, question = "Kannst du deinen Passwort Manager auf verschiedenen Geräten nutzen?", image = null, options = listOf("Ja", "Nein"), feedbacks = listOf()),
    Question(id = 7, question = "Nutzt du Autofill Funktion beim Login in deine Kontos?", image = null, options = listOf("Ja", "Nein"), feedbacks = listOf()),
    Question(id = 8, question = "Hast du ein Browser Plugin mit deinem Passwort Manager?", image = null, options = listOf("Ja", "Nein"), feedbacks = listOf()),
    Question(id = 9, question = "Fühlst du dich sicherer seitdem du einen Passwort Manager nutzt?", image = null, options = listOf("Ja", "Nein"), feedbacks = listOf())
)

val nonPasswordManagerUserQuestions = listOf(
    Question(id = 10, question = "Welches Passwort findest du gut?", image = null, options = listOf("123456", "f&M3#p12k", "S0mm3r"), feedbacks = listOf()),
    Question(id = 11, question = "Tipps du deine Passwörter jedes Mal beim Login manuell ein?", image = null, options = listOf("Ja", "Nein"), feedbacks = listOf()),
    Question(id = 12, question = "Hast du schonmal Passwörter vergessen oder musstest sie zurücksetzen?", image = null, options = listOf("Ja", "Nein"), feedbacks = listOf()),
    Question(id = 13, question = "Wärst du bereit einen Passwort Manager auszuprobieren?", image = null, options = listOf("Ja", "Nein"), feedbacks = listOf())
)

fun getQuestionsForUser(usesPasswordManager: Boolean): List<Question> {
    return if (usesPasswordManager) {
        generalQuestions + passwordManagerUserQuestions
    } else {
        generalQuestions + nonPasswordManagerUserQuestions
    }
}

