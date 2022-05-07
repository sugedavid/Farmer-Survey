package com.sogoamobile.farmersurvey.network

data class Question (
    val id: String ,
    val question_type: String,
    val question_text: String,
    val options: List<Options>,
)
