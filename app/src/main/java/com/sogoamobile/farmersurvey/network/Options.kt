package com.sogoamobile.farmersurvey.network

data class Options (
    val value: String,
    val display_text: String,
){
    override fun toString(): String {
        return value
    }
}

