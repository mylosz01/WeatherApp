package com.example.weatherapp

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class WeatherWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext,workerParams) {

    override fun doWork(): Result {
        return try {
            fetchWeatherData()
            scheduleNextWork()
            Result.success()
        }
        catch (e: Exception){
            Result.retry()
        }
    }

    private fun fetchWeatherData() {
        Log.d("DEBUG","REFRESH WEATHER DATA BY THREAD!!")
    }

    private fun scheduleNextWork() {
        val workRequest = OneTimeWorkRequestBuilder<WeatherWorker>()
            .setInitialDelay(5, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "WeatherWorker",
            ExistingWorkPolicy.REPLACE,
            workRequest)
    }
}