package com.example.remember

import android.annotation.SuppressLint
import android.content.Context
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

@SuppressLint("MissingPermission")
class UpdateGpsWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params){
    companion object {
        private val TAG = UpdateGpsWorker::class.java.name
    }

    private val mContext = context
    //위치 가져올때 필요
    private val mFusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(mContext)
    }

    override suspend fun doWork(): Result {
        try {
            val handlerThread = HandlerThread(TAG)
            handlerThread.start()
            mFusedLocationClient.requestLocationUpdates(
                createLocationRequest(),
                createLocationCallback(),
                handlerThread.looper
            )
        } catch (err: ApiException) {
            Log.e(TAG, err.toString())
            Result.failure()
        }
        return Result.success()
    }

    private fun createLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setIntervalMillis(10000)
            .setMinUpdateIntervalMillis(5000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        return locationRequest
    }

    private  fun createLocationCallback(): LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult ?: return
            for (location in locationResult.locations){
                Log.i(TAG, "New location $location")
            }
        }
    }
}