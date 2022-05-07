package com.sogoamobile.farmersurvey.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sogoamobile.farmersurvey.network.FarmerSurvey
import com.sogoamobile.farmersurvey.network.FarmerSurveyApi
import kotlinx.coroutines.launch

enum class FarmerSurveyApiStatus { LOADING, ERROR, DONE }

class SurveyViewModel : ViewModel() {

    private val _status = MutableLiveData<FarmerSurveyApiStatus>()

    private val _survey = MutableLiveData<FarmerSurvey>()
    val survey: LiveData<FarmerSurvey> = _survey

    private var _index = MutableLiveData<Int>(0)
    val index: LiveData<Int> = _index

    init {
        getSurvey()
    }

    private fun getSurvey() {
        viewModelScope.launch {
            _status.value = FarmerSurveyApiStatus.LOADING
            try {
                _survey.value = FarmerSurveyApi.retrofitService.getSurvey()
                _status.value = FarmerSurveyApiStatus.DONE
            } catch (e: Exception) {
                _status.value = FarmerSurveyApiStatus.ERROR
                _survey.value = FarmerSurvey("","", listOf(), mapOf())
            }
        }
    }

    fun incrementIndex(questionsSize: Int){
        if(_index.value ?: 0 < questionsSize) {
            _index.value = _index.value?.plus(1)
        }
    }

     fun decrementIndex(){
        if(_index.value?: 0 > 0) {
            _index.value = _index.value?.minus(1)
        }
    }
}