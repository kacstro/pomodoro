package com.example.pomodoro

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var typeTimeText: TextView
    private lateinit var bgLayout: RelativeLayout
    private var countDownTimer: CountDownTimer? = null
    private var totalDurationInMillis: Long = 25 * 60 * 1000
    private var timeFocusDuration: Long = 25 * 60 * 1000
    private var timeRestDuration: Long = 5 * 60 * 1000
    private var timeLeftInMillis: Long = 0L
    private var isTimerRunning = false
    private var timeFocus: String = "25:00"
    private var timeRest: String = "05:00"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_work)

        timerTextView = findViewById(R.id.timerTextView)
        progressBar = findViewById(R.id.progress_circular)
        typeTimeText = findViewById(R.id.typeTimeText)
        bgLayout = findViewById(R.id.bg_layout)

        loadTimeSettings()
        timeFocusDuration = convertToMillis(timeFocus)
        timeRestDuration = convertToMillis(timeRest)

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
            val settingsDialog = SettingsDialog()
            settingsDialog.setOnDismissListener { newTimeFocus, newTimeRest ->
                countDownTimer?.cancel()
                progressBar.progress = 100
                isTimerRunning = false
                playButton.visibility = View.VISIBLE
                pauseButton.visibility = View.GONE

                timeFocus = newTimeFocus
                timeRest = newTimeRest

                timeFocusDuration = convertToMillis(timeFocus)
                timeRestDuration = convertToMillis(timeRest)

                totalDurationInMillis = timeFocusDuration
                timerTextView.text = timeFocus

                Toast.makeText(this, "Tempo atualizado para Foco: $timeFocus e Descanso: $timeRest", Toast.LENGTH_SHORT).show()
            }
            settingsDialog.show(supportFragmentManager, "settingsDialog")
        }
    }

    private fun setProgress(millisUntilFinished: Long, durationInMillis: Long): Int {
        val seconds = (millisUntilFinished / 1000) % 60
        return if(seconds > 0){
            ((millisUntilFinished.toFloat() / durationInMillis) * 100).toInt()
        }else{
            0
        }
    }

    private fun startTimer(durationInMillis: Long, isFocusTime: Boolean = true) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(durationInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                timerTextView.text = String.format("%02d:%02d", minutes, seconds)

                val progress = if(!isTimerRunning){
                    setProgress(millisUntilFinished, durationInMillis)
                }else{
                    setProgress(millisUntilFinished, totalDurationInMillis)
                }
                progressBar.progress = progress
            }

            override fun onFinish() {
                timerTextView.text = getString(R.string.tempo_zerado)
                progressBar.progress = 0
                isTimerRunning = false

                if (isFocusTime) {
                    typeTimeText.text = getString(R.string.pausa)
                    bgLayout.setBackgroundResource(R.color.bg_blue)
                    startTimer(timeRestDuration, isFocusTime = false)
                } else {
                    typeTimeText.text = getString(R.string.foco)
                    bgLayout.setBackgroundResource(R.color.bg_purple)
                    startTimer(timeFocusDuration, isFocusTime = true)
                }
            }
        }.start()
    }

    private fun loadTimeSettings() {
        val sharedPreferences = getSharedPreferences("PomodoroPrefs", Context.MODE_PRIVATE)
        timeFocus = sharedPreferences.getString("timeFocus", "25:00") ?: "25:00"
        timeRest = sharedPreferences.getString("timeRest", "05:00") ?: "05:00"
    }

    private fun convertToMillis(time: String): Long {
        val parts = time.split(":")
        val minutes = parts[0].toIntOrNull() ?: 0
        val seconds = parts[1].toIntOrNull() ?: 0
        return (minutes * 60 * 1000L) + (seconds * 1000L)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}