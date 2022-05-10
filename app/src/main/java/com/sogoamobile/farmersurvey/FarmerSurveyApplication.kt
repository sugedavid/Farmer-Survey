package com.sogoamobile.farmersurvey

import android.app.Application
import com.sogoamobile.farmersurvey.database.SurveyDatabase

/**
 * Override application to setup background work via WorkManager
 */
class FarmerSurveyApplication : Application() {
    val database: SurveyDatabase by lazy { SurveyDatabase.getDatabase(this) }
}
