package com.sogoamobile.farmersurvey.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sogoamobile.farmersurvey.network.Options
import com.sogoamobile.farmersurvey.network.Question
import com.sogoamobile.farmersurvey.network.QuestionRepo

@Entity(tableName = "farmer_survey")
data class FarmerSurveyTable constructor(
        @PrimaryKey
        val id: String,
        val start_question_id: String,
)

@Entity(tableName = "questions")
data class QuestionsTable constructor(
        @PrimaryKey
        val id: String,
        val question_type: String,
        val question_text: String,
)

@Entity(tableName = "options")
data class OptionsTable constructor(
        @PrimaryKey
        val value: String,
        val display_text: String,
){
        override fun toString(): String {
                return value
        }
}


@Entity(tableName = "strings")
data class StringsTable constructor(
        @PrimaryKey
        val id: String,
        val en: String,
)

/**
 * Map DatabaseVideos to domain entities
 */
fun List<QuestionsTable>.asQuestionsDatabaseModel(): List<QuestionRepo> {
        return map {
                QuestionRepo(
                        id = it.id,
                        question_type = it.question_type,
                        question_text = it.question_text,)
        }
}
