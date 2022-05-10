package com.sogoamobile.farmersurvey.network

data class FarmerSurvey(
    val id: String,
    var start_question_id:String,
    val questions: List<Question>,
    val strings: Map<String,Map<String,String>>,
)

