package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

// Notification ID.
private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, fileName: String, status: String) {
    // create intent
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
            .putExtra("fileName", fileName)
            .putExtra("status", status)


    //create PendingIntent
    val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )

    // TODO: add style

    // Get an instance of NotificationCompat.Builder
    // Build the notification
    val notificationBuilder = NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.channel_id))
            .setSmallIcon(R.drawable.ic_download_24dp)
            .setContentTitle(applicationContext
                    .getString(R.string.notification_title))
            .setContentText(messageBody)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            // Set the action to open the DetailActivity
            .addAction(0,
                    applicationContext.getString(R.string.notification_button),
                    contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

            /*//  add style to builder
            .setStyle()
            .setLargeIcon()*/

    notify(NOTIFICATION_ID, notificationBuilder.build())
}
// fun to cancel all notifications
fun NotificationManager.cancelNotifications() {
    cancelAll()

}