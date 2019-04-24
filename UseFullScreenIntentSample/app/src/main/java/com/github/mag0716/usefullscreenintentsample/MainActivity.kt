package com.github.mag0716.usefullscreenintentsample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "UseFullScreenIntent"
        private const val CHANNEL_ID = "Channel"
    }

    private lateinit var startActivityInBackgroundButton: Button
    private lateinit var notificationButton: Button
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivityInBackgroundButton = findViewById(R.id.start_activity_in_background_button)
        startActivityInBackgroundButton.setOnClickListener {
            handler.postDelayed({
                startActivityInBackground()
            }, 3000)
            finish()
        }
        notificationButton = findViewById(R.id.notification_button)
        notificationButton.setOnClickListener {
            handler.postDelayed({
                notifyFullScreenIntentNotification()
            }, 3000)
        }
    }

    private fun startActivityInBackground() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun notifyFullScreenIntentNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelIds = notificationManager.notificationChannels.map { it.id }.toList()
            if (channelIds.contains(CHANNEL_ID).not()) {
                val channel = NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_ID,
                        NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }
        }

        val fullScreenIntent = Intent(this, MainActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(this,
                0,
                fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle("Content Title")
                .setContentText("Content Text")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Heads-up で表示され自動的に消えずにずっと表示されている
                .setFullScreenIntent(fullScreenPendingIntent, true)
        val notification = notificationBuilder.build()
        notificationManager.notify(0, notification)
    }
}
