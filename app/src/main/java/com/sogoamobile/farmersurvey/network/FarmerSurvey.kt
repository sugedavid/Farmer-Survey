package com.sogoamobile.farmersurvey.network

import com.sogoamobile.farmersurvey.database.FarmerSurveyTable
import com.sogoamobile.farmersurvey.database.OptionsTable
import com.sogoamobile.farmersurvey.database.QuestionsTable
import com.sogoamobile.farmersurvey.database.StringsTable
import com.squareup.moshi.JsonClass

data class FarmerSurvey(
    val id: String,
    var start_question_id:String,
    val questions: List<Question>,
    val strings: Map<String,Map<String,String>>,
)

@JsonClass(generateAdapter = true)
data class NetworkVideoContainer(val questions: List<QuestionRepo>, val options: List<OptionsTable>,
                                 val strings: List<StringsTable>)


/**
 * Convert Network results to database objects
 */
fun NetworkVideoContainer.asQuestionsDatabaseModel(): List<QuestionsTable> {
    return questions.map {
        QuestionsTable(
            id = it.id,
            question_type = it.question_type,
            question_text = it.question_text,
            )
    }
}

fun NetworkVideoContainer.asOptionsDatabaseModel(): List<OptionsTable> {
    return options.map {
        OptionsTable(
            value = it.value,
            display_text = it.display_text,
        )
    }
}

fun NetworkVideoContainer.asStringsDatabaseModel(): List<StringsTable> {
    return strings.map {
        StringsTable(
            id = it.id,
            en = it.en,
        )
    }
}

@JsonClass(generateAdapter = true)
data class QuestionRepo (
    val id: String ,
    val question_type: String,
    val question_text: String,
)