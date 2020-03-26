package com.anilpathak475.staxter.notification

import android.app.Notification
import android.content.Context

interface SimpleNotificationManager {
    fun createSimpleNotification(context: Context, notificationData: NotificationData): Notification
}
