package com.example.weatherapp_ramide.monitor

import android.app.NotificationManager
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weatherapp_ramide.model.City
import java.util.concurrent.TimeUnit

class ForecastMonitor(context: Context) {
    private val wm = WorkManager.getInstance(context)
    private val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun updateCity(city: City) {
        cancelCity(city)

        if (!city.isMonitored) return
        val inputData = workDataOf("city" to city.name)
        val request = PeriodicWorkRequestBuilder<ForecastWorker>(
            repeatInterval = 15, repeatIntervalTimeUnit = TimeUnit.MINUTES
        ).setInitialDelay(
            duration = 10, timeUnit = TimeUnit.SECONDS
        ).setInputData(inputData).build()
        wm.enqueueUniquePeriodicWork(
            city.name,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, request
        )
    }

    fun cancelCity(city: City) {
        wm.cancelUniqueWork(city.name)
        nm.cancel(city.name.hashCode())
    }

    fun cancelAll() {
        wm.cancelAllWork()
        nm.cancelAll()
    }
}
