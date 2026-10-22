package com.example.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.GenderGroup
import com.example.data.MotivationalLibrary
import kotlin.random.Random

object NotificationHelper {
    private const val CHANNEL_ID = "wird_flash_reminders_channel"
    private const val CHANNEL_NAME = "تذكيرات وِرد الخاطفة"
    private const val CHANNEL_DESC = "عبارات ذكر قصيرة جداً وخاطفة تذكرك بالله بلطف دون إزعاج"

    fun ensureChannelCreated(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESC
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun sendFlashDhikrNotification(
        context: Context,
        genderGroup: GenderGroup,
        index: Int = Random.nextInt(100)
    ): Pair<String, String> {
        ensureChannelCreated(context)
        val phrases = MotivationalLibrary.getFlashNotificationPhrases(genderGroup)
        val chosen = phrases[index.mod(phrases.size)]

        if (hasNotificationPermission(context)) {
            runCatching {
                val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(chosen.first)
                    .setContentText(chosen.second)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(chosen.second))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build()

                NotificationManagerCompat.from(context).notify(2026 + (index % 5), notification)
            }
        }
        return chosen
    }
}
