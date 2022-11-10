package com.androidrey.currencyexchange.workmanager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.androidrey.currencyexchange.model.Rate
import com.androidrey.currencyexchange.repository.RatesRepository
import com.androidrey.currencyexchange.util.Status
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context, @Assisted workerParams: WorkerParameters,
    private val repo: RatesRepository
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        Log.i("Worker", "Started")
        return when (val status = repo.getRateResponseFromServer()) {
            is Status.Success -> {
                val ratesList: MutableList<Rate> = mutableListOf()
                status.data?.rates?.let {
                    it.forEach { entry ->
                        ratesList.add(
                            Rate(
                                entry.key,
                                entry.value
                            )
                        )
                    }
                    repo.insertRatesToLocalDB(ratesList)
                }
                Log.i("Worker", "Success")
                Result.success()
            }
            is Status.Error -> {
                Log.i("Worker", "failure")
                Result.failure()
            }
            else -> {
                Log.i("Worker", "failure")
                Result.failure()
            }
        }
    }
}