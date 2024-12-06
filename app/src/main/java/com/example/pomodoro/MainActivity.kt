package com.example.pomodoro

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var progressBar: ProgressBar
    private var countDownTimer: CountDownTimer? = null
    private val totalDurationInMillis: Long = 30 * 60 * 1000
    private var timeLeftInMillis: Long = 0L
    private var isTimerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_work)

        timerTextView = findViewById(R.id.timerTextView)
        progressBar = findViewById(R.id.progress_circular)

        timerTextView.text = "30:00"
        progressBar.max = 100

        val resetButton: ImageButton = findViewById(R.id.button_reset)
        val pauseButton: ImageButton = findViewById(R.id.button_pause)
        val playButton: ImageButton = findViewById(R.id.button_play)
        val settingsButton: ImageButton = findViewById(R.id.button_settings)

        resetButton.setOnClickListener {
            isTimerRunning = false
            countDownTimer?.cancel()
            startTimer(totalDurationInMillis)
        }

        playButton.setOnClickListener {
            playButton.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
            if(!isTimerRunning){
                startTimer(totalDurationInMillis)
            }else{
                startTimer(timeLeftInMillis)
            }
        }

        pauseButton.setOnClickListener {
            isTimerRunning = true
            playButton.visibility = View.VISIBLE
            pauseButton.visibility = View.GONE
            countDownTimer?.cancel()
        }

        settingsButton.setOnClickListener {
            // Abrir configurações
        }
    }

    private fun startTimer(durationInMillis: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(durationInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                timerTextView.text = String.format("%02d:%02d", minutes, seconds)

                val progress = ((millisUntilFinished.toFloat() / totalDurationInMillis) * 100).toInt()
                progressBar.progress = progress
            }

            override fun onFinish() {
                timerTextView.text = "00:00"
                progressBar.progress = 0
                isTimerRunning = false
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}