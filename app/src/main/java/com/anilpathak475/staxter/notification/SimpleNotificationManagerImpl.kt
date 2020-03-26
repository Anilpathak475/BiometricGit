package com.anilpathak475.staxter.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.anilpathak475.staxter.R

class SimpleNotificationManagerImpl : SimpleNotificationManager {
    override fun createSimpleNotification(
        context: Context,
        notificationData: NotificationData
    ): Notification {
        val builder = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        ).apply {
            setSmallIcon(R.drawable.ic_launcher_background)
            setContentTitle(notificationData.contentTitle)
            setContentText(notificationData.contentText)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = notificationData.channelName
            val descriptionText = notificationData.channelDescription
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        return builder.build()
    }

    companion object {
        private const val CHANNEL_ID = "staxter_data_changer_channel"
    }

}