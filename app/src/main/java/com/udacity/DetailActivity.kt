package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        // Initialize Notification Manager
        notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
        ) as NotificationManager

        notificationManager.cancelAll()

        val downloadStatus = intent.getStringExtra("status")

        file_name.text = intent.getStringExtra("fileName")
        status.text = intent.getStringExtra("status")

        if (downloadStatus == getString(R.string.status_fail)){
            status.setTextColor(ContextCompat.getColor(this,R.color.status_failed))
        }

    }

}
