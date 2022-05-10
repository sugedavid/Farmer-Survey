package com.sogoamobile.farmersurvey.database

import androidx.room.*

@Entity(tableName = "user_profile")
data class UserProfileTable constructor(
        @PrimaryKey
        val id: String,
        val isLoggedIn: Boolean,
)

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

@Entity(tableName = "survey_responses")
data class SurveyResponseTable constructor(
        @PrimaryKey
        val id: String,
        val answer: String,
)
