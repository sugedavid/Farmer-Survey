package com.sogoamobile.farmersurvey.viewmodel

import android.widget.Toast
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    fun isPhoneNumberValid(phoneNumber: String) : Boolean{
        if (phoneNumber.startsWith("+254")){
            return true
        }
        return false
    }

    fun isPasswordValid(password: String) : Boolean{
        if (password == "1234GYD%$"){
            return true
        }
        return false
    }
}