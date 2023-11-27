package com.example.remember

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
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
            mFusedLocationClient.getCurrentLocation(
                createCurrentLocationRequest(Long.MAX_VALUE, 0L),
                createCancellationToken()
            ).addOnCompleteListener {task ->
                if(task.isSuccessful) {
                    task.result?.let { aLocation ->
                        val fromLat = aLocation.latitude
                        val fromLng = aLocation.longitude
                        Log.d(TAG, "task성공, 위도: $fromLat, 경도: $fromLng")
                    }
                } else {
                    Log.e(TAG, "task 실패")
                }
            }
        } catch (err: ApiException) {
            Log.e(TAG, err.toString())
            Result.failure()
        }
        return Result.success()
    }

    private fun createCancellationToken(): CancellationToken =
        object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener):
                    CancellationToken = CancellationTokenSource().token
            override fun isCancellationRequested(): Boolean = false
        }

    private fun createCurrentLocationRequest(limitTimeMills: Long, cachingExpiresInMills: Long): CurrentLocationRequest =
        CurrentLocationRequest.Builder()
            .setDurationMillis(limitTimeMills)
            .setMaxUpdateAgeMillis(cachingExpiresInMills)
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .build()
}