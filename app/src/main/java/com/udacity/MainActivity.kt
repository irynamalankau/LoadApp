package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var notificationMessageBody: String = ""
    private var fileName: String = ""

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        // Initialize the notification manager
        notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
        ) as NotificationManager


        custom_button.setOnClickListener {
            when (radioGroup.checkedRadioButtonId) {
                R.id.radio_button_glide -> onRadioButtonChecked(URL_GLIDE,
                        R.string.notification_description_glide,
                        R.string.file_name_glide)

                R.id.radio_button_udacity -> onRadioButtonChecked(URL_UDACITY,
                        R.string.notification_description_udacity,
                        R.string.file_name_udacity)

                R.id.radio_button_retrofit -> onRadioButtonChecked(URL_RETROFIT,
                        R.string.notification_description_retrofit,
                        R.string.file_name_retrofit)

                else -> Toast.makeText(applicationContext, getString(R.string.toast_message), Toast.LENGTH_SHORT).show()

            }
        }
        //create notification channel
        createChannel(
                applicationContext.getString(R.string.channel_id),
                applicationContext.getString(R.string.channel_name)
        )
    }
        //create broadcast receiver
        private val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadID) {
                    context?.getString(R.string.status_success)?.let { sendNotificationWithStatus(it) }
                } else {
                    context?.getString(R.string.status_failed)?.let { sendNotificationWithStatus(it) }
                }
            }
        }

        private fun download(url: String) {
            val request =
                    DownloadManager.Request(Uri.parse(url))
                            .setTitle(getString(R.string.app_name))
                            .setDescription(getString(R.string.app_description))
                            .setRequiresCharging(false)
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true)

            // Set button state, start animation
            custom_button.buttonState = ButtonState.Loading

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID = downloadManager.enqueue(request)// enqueue() puts the download request in the queue. enqueue() returns an ID for the download, unique across the system
        }


    //fun to create notification channel
    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            )
                    .apply {
                        setShowBadge(false)
                    }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.app_description)

            val notificationManager = this.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    private fun onRadioButtonChecked(url: String, resourceIdMessageBody: Int, resourceIdFileName: Int) {
        download(url)
        notificationMessageBody = applicationContext.getString(resourceIdMessageBody)
        fileName = applicationContext.getString(resourceIdFileName)
    }

    private fun sendNotificationWithStatus(status: String) {
        //set button state and stop animation
        custom_button.buttonState = ButtonState.Completed
        notificationManager.sendNotification(notificationMessageBody, applicationContext, fileName, status)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    companion object {
        private const val URL_GLIDE =
                "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_UDACITY =
                "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_RETROFIT =
                "https://github.com/square/retrofit/archive/master.zip"

    }

}
