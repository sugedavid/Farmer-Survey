package com.sogoamobile.farmersurvey.viewmodel

import android.widget.Toast
import androidx.lifecycle.*
import com.sogoamobile.farmersurvey.database.FarmerSurveyDao
import com.sogoamobile.farmersurvey.database.QuestionsTable
import com.sogoamobile.farmersurvey.database.UserProfileTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel (private val surveyDao: FarmerSurveyDao) : ViewModel() {

    val isLoggedIn= surveyDao.getUserProfile().asLiveData()

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

    fun updateIsLoggedIn(value: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            surveyDao.insertToUserProfile(UserProfileTable("", value))
        }
    }

}

class LoginViewModelFactory(private val farmerSurveyDao: FarmerSurveyDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(farmerSurveyDao) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}