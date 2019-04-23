package com.github.mag0716.usefullscreenintentsample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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

    private lateinit var button: Button
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button)
        button.setOnClickListener {
            handler.postDelayed(Runnable {
                notifyFullScreenIntentNotification()
            }, 3000)
        }
    }

    private fun notifyFullScreenIntentNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelIds = notificationManager.notificationChannels.map { it.id }.toList()
        if (channelIds.contains(CHANNEL_ID).not()) {
            val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_ID,
                    NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val fullScreenIntent = Intent(this, MainActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(this,
                0,
                fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
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
