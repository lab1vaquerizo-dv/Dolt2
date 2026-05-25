package com.example.dolt2

import android.app.Application
import androidx.work.Configuration
import com.example.dolt2.notifications.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DoltApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var hiltWorkManagerConfiguration: Configuration

    override val workManagerConfiguration: Configuration
        get() = hiltWorkManagerConfiguration

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}