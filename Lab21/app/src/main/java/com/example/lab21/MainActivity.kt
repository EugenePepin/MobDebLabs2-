package com.example.lab21

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var questionText: TextView
    private lateinit var colorNameText: TextView
    private lateinit var resultText: TextView
    private lateinit var yesButton: Button
    private lateinit var noButton: Button
    private lateinit var continueButton: Button

    private val colorNames = listOf("Червоний", "Зелений", "Синій", "Жовтий")
    private val colorValues = listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)
    private var correctAnswers = 0
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        questionText = findViewById(R.id.question_text)
        colorNameText = findViewById(R.id.color_name_text)
        resultText = findViewById(R.id.result_text)
        yesButton = findViewById(R.id.yes_button)
        noButton = findViewById(R.id.no_button)
        continueButton = findViewById(R.id.continue_button)

        yesButton.setOnClickListener { checkAnswer(true) }
        noButton.setOnClickListener { checkAnswer(false) }
        continueButton.setOnClickListener { continueGame() }

        startGame()
    }

    private fun startGame() {
        correctAnswers = 0
        questionText.visibility = TextView.VISIBLE
        colorNameText.visibility = TextView.VISIBLE
        resultText.visibility = TextView.GONE
        continueButton.visibility = Button.GONE
        yesButton.isEnabled = true
        noButton.isEnabled = true

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
        val randomColorNameIndex = Random.nextInt(colorNames.size)
        val randomColorValueIndex = Random.nextInt(colorValues.size)

        colorNameText.text = colorNames[randomColorNameIndex]
        colorNameText.setTextColor(colorValues[randomColorValueIndex])
    }

    private fun checkAnswer(isYesSelected: Boolean) {
        val colorMatches = colorNameText.text.toString() == getColorNameFromColor(colorNameText.currentTextColor)

        if (isYesSelected == colorMatches) {
            correctAnswers++
        }

        generateNewQuestion()
    }

    private fun getColorNameFromColor(color: Int): String {
        return when (color) {
            Color.RED -> "Червоний"
            Color.GREEN -> "Зелений"
            Color.BLUE -> "Синій"
            Color.YELLOW -> "Жовтий"
            else -> ""
        }
    }

    private fun endGame() {
        questionText.visibility = TextView.GONE
        colorNameText.visibility = TextView.GONE
        resultText.text = "Кількість правильних відповідей: $correctAnswers"
        resultText.visibility = TextView.VISIBLE
        continueButton.visibility = Button.VISIBLE

        yesButton.isEnabled = false
        noButton.isEnabled = false

    }

    private fun continueGame() {
        resultText.visibility = TextView.GONE
        continueButton.visibility = Button.GONE
        startGame()
    }

}