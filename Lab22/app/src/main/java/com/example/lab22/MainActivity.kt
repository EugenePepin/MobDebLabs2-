package com.example.lab22

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var questionText: TextView
    private lateinit var correctButton: Button
    private lateinit var incorrectButton: Button
    private lateinit var continueButton: Button
    private lateinit var resultText: TextView
    private lateinit var withRepeatButton: Button
    private lateinit var withoutRepeatButton: Button

    private val questions = listOf(
        Questions("Гурт The Beatles був заснований у Великобританії", true),
        Questions("Гурт Nirvana був заснований у Канаді", false),
        Questions("Гурт Queen мав вокаліста Фредді Мерк'юрі", true),
        Questions("Гурт Metallica грає джазову музику", false)
    )

    private val askedQuestions = mutableSetOf<Int>()
    private var allowRepeats = true
    private var correctAnswers = 0
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        questionText = findViewById(R.id.question_text)
        correctButton = findViewById(R.id.correct_button)
        incorrectButton = findViewById(R.id.incorrect_button)
        continueButton = findViewById(R.id.continue_button)
        resultText = findViewById(R.id.result_text)
        withRepeatButton = findViewById(R.id.with_repeat_button)
        withoutRepeatButton = findViewById(R.id.without_repeat_button)

        correctButton.setOnClickListener { checkAnswer(true) }
        incorrectButton.setOnClickListener { checkAnswer(false) }
        continueButton.setOnClickListener { showGameModeSelection() }

        withRepeatButton.setOnClickListener {
            allowRepeats = true
            startGame()
        }
        withoutRepeatButton.setOnClickListener {
            allowRepeats = false
            startGame()
        }

        showGameModeSelection()
    }

    private fun showGameModeSelection() {
        withRepeatButton.visibility = Button.VISIBLE
        withoutRepeatButton.visibility = Button.VISIBLE
        questionText.visibility = TextView.GONE
        correctButton.visibility = Button.GONE
        incorrectButton.visibility = Button.GONE
        continueButton.visibility = Button.GONE
        resultText.visibility = TextView.GONE
    }

    private fun startGame() {
        correctAnswers = 0
        askedQuestions.clear()
        resultText.visibility = TextView.GONE
        continueButton.visibility = Button.GONE
        correctButton.isEnabled = true
        incorrectButton.isEnabled = true

        withRepeatButton.visibility = Button.GONE
        withoutRepeatButton.visibility = Button.GONE
        questionText.visibility = TextView.VISIBLE
        correctButton.visibility = Button.VISIBLE
        incorrectButton.visibility = Button.VISIBLE

        timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                endGame()
            }
        }.start()

        generateNewQuestion()
    }

    private fun generateNewQuestion() {
        if (!allowRepeats && askedQuestions.size == questions.size) {
            endGame()
            return
        }

        var questionIndex: Int
        do {
            questionIndex = Random.nextInt(questions.size)
        } while (!allowRepeats && askedQuestions.contains(questionIndex))

        if (!allowRepeats) {
            askedQuestions.add(questionIndex)
        }

        val question = questions[questionIndex]
        questionText.text = question.text
    }

    private fun checkAnswer(isCorrectSelected: Boolean) {
        val currentQuestion = questions.find { it.text == questionText.text }
        if (currentQuestion != null) {
            if (currentQuestion.isCorrect == isCorrectSelected) {
                correctAnswers++
            }
            generateNewQuestion()
        }
    }

    private fun endGame() {
        questionText.visibility = TextView.GONE
        resultText.text = "Кількість правильних відповідей: $correctAnswers"
        resultText.visibility = TextView.VISIBLE
        continueButton.visibility = Button.VISIBLE

        correctButton.isEnabled = false
        incorrectButton.isEnabled = false

    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}