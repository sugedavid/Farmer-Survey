/*
 * Copyright (C) 2019 Google Inc.
 *gi
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sogoamobile.farmersurvey.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.sogoamobile.farmersurvey.database.*
import kotlinx.coroutines.coroutineScope

/**
 * Repository for fetching devbyte videos from the network and storing them on disk
 */

class FarmerSurveyRepository(private val surveyDao: FarmerSurveyDao)  {

    val readQuestions = surveyDao.getQuestions()
    val readOptions = surveyDao.getOptions()

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

//     fun retrieveString(id: String): LiveData<StringsTable> {
//        return surveyDao.getStrings(id).asLiveData()
//    }
}
