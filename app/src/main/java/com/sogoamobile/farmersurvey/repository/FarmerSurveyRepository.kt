package com.sogoamobile.farmersurvey.repository

import com.sogoamobile.farmersurvey.database.*
import kotlinx.coroutines.coroutineScope

/**
 * Repository for fetching survey from the network and storing them on disk
 */

class FarmerSurveyRepository(private val surveyDao: FarmerSurveyDao)  {

    val readQuestions = surveyDao.getQuestions()
    val readOptions = surveyDao.getOptions()
    val readSurveyResponse = surveyDao.getSurveyResponse()

    suspend fun addQuestion(question: QuestionsTable) {
        coroutineScope {
            surveyDao.insertToQuestions(question)
        }
    }

    suspend fun addStrings(stringsTable: StringsTable) {
        coroutineScope {
            surveyDao.insertToStrings(stringsTable)
        }
    }

    suspend fun addOptions(optionsTable: OptionsTable) {
        coroutineScope {
            surveyDao.insertToOptions(optionsTable)
        }
    }

    suspend fun addSurvey(surveyTable: FarmerSurveyTable) {
        coroutineScope {
            surveyDao.insertToSurvey(surveyTable)
        }
    }

    suspend fun addSurveyResponse(surveyResponseTable: SurveyResponseTable) {
        coroutineScope {
            surveyDao.insertToSurveyResponse(surveyResponseTable)
        }
    }

}
