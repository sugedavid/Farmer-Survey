package com.sogoamobile.farmersurvey.database

import androidx.lifecycle.LiveData
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToSurvey( farmerSurvey: FarmerSurveyTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToQuestions( questionsTable: QuestionsTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToOptions( optionsTable: OptionsTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertToStrings( stringsTable: StringsTable)

}