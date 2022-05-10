package com.sogoamobile.farmersurvey.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmerSurveyDao {
    @Query("select * from questions")
    fun getQuestions(): Flow<List<QuestionsTable>>

    @Query("select * from options")
    fun getOptions(): Flow<List<OptionsTable>>

    @Query("select * from strings where id =:id")
    fun getStrings(id: String): Flow<StringsTable>

    @Query("select * from farmer_survey")
    fun getSurvey(): Flow<FarmerSurveyTable>

    @Query("select * from user_profile")
    fun getUserProfile(): Flow<UserProfileTable>

    @Query("select * from survey_responses")
    fun getSurveyResponse(): Flow<List<SurveyResponseTable>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToSurvey( farmerSurvey: FarmerSurveyTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToQuestions( questionsTable: QuestionsTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToOptions( optionsTable: OptionsTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToStrings( stringsTable: StringsTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToUserProfile( userProfileTable: UserProfileTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToSurveyResponse(surveyResponseTable: SurveyResponseTable)

}