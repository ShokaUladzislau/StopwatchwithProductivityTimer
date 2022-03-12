package org.hyperskill.stopwatch

import android.app.AlertDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import java.lang.Runnable


const val CHANNEL_ID = "org.hyperskill"

class MainActivity : AppCompatActivity() {

    var seconds = 0
    var minutes = 0
    var limitTime = 0
    val colors = arrayOf(Color.RED, Color.GREEN, Color.BLUE)
    var color = colors[0]

    var notificated = false

    val timerHandler = Handler(Looper.getMainLooper())
    val notificationHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView: TextView = findViewById(R.id.textView)
        val defaultTextColor = textView.textColors
        val startButton: Button = findViewById(R.id.startButton)
        val resetButton: Button = findViewById(R.id.resetButton)
        val settingsButton: Button = findViewById(R.id.settingsButton)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.abc_vector_test)
                .setContentTitle("Notification")
                .setContentText("Time exceeded")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        progressBar.visibility = View.GONE

        val updateTime: Runnable = object : Runnable {
            override fun run() {
                seconds++
                when {
                    seconds == 60 -> {
                        minutes++
                        seconds = 0
                    }
                    minutes == 60 -> {
                        minutes = 0
                    }
                }

                if (limitTime != 0 && limitTime < minutes * 60 + seconds) {
                    textView.setTextColor(Color.RED)

                    if(notificated) {
                        notificationHandler.postDelayed({ notificationManager.cancel(393939) }, 5000)
                    } else {
                        notificationManager.notify(393939, builder.build())
                        notificated = true
                    }
                }

                color = colors[(colors.indexOf(color) + 1) % colors.size]
                progressBar.indeterminateTintList = ColorStateList.valueOf(color)
                textView.text = String.format("%02d:%02d", minutes, seconds)
                timerHandler.postDelayed(this, 1000)
            }
        }

        startButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            settingsButton.isEnabled = false
            if (!timerHandler.hasCallbacks(updateTime)) {
                timerHandler.postDelayed(updateTime, 1000)
            }
        }

        resetButton.setOnClickListener {
            textView.setTextColor(defaultTextColor)
            notificated = false
            progressBar.visibility = View.GONE
            settingsButton.isEnabled = true
            minutes = 0
            seconds = 0
            limitTime = 0
            textView.text = String.format("%02d:%02d", minutes, seconds)
            timerHandler.removeCallbacks(updateTime)
        }

        settingsButton.setOnClickListener {
            val contentView = LayoutInflater.from(this).inflate(R.layout.settings_btn_alert_dialog, null, false)
            AlertDialog.Builder(this)
                    .setTitle("Alert Dialog with Custom View!")
                    .setView(contentView)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        val editText = contentView.findViewById<EditText>(R.id.upperLimitEditText)
                        limitTime = editText.text.toString().toInt()
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->
                    }
                    .show()
        }
    }
}