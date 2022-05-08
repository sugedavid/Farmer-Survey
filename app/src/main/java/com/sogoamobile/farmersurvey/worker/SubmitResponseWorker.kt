package com.sogoamobile.farmersurvey.worker

import android.content.Context
import android.os.CountDownTimer
import android.widget.Toast
import androidx.lifecycle.asLiveData
import androidx.work.Worker
import androidx.work.WorkerParameters

import com.sogoamobile.farmersurvey.FarmerSurveyApplication
import com.sogoamobile.farmersurvey.network.FarmerSurveyApi

private const val TAG = "SubmitResponseWorker"
class SubmitResponseWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val appContext = applicationContext
        val surveyResponse = FarmerSurveyApplication().database.surveyDao().getSurveyResponse()

//        makeStatusNotification("Submitting Response", appContext)

        return try {
            surveyResponse.asLiveData().value?.let {
                FarmerSurveyApi.retrofitService.postSurvey(
                    it
                )
            }
            Result.success()
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            Result.failure()
        }
    }

}