package com.anilpathak475.staxter.notification

data class NotificationData(
    val channelName: String? = null,
    val channelDescription: String? = null,
    val contentTitle: String,
    val contentText: String

)