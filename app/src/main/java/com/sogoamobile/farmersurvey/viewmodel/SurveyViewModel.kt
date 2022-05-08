package com.sogoamobile.farmersurvey.viewmodel


import androidx.lifecycle.*
import com.sogoamobile.farmersurvey.database.*
import com.sogoamobile.farmersurvey.network.FarmerSurvey
import com.sogoamobile.farmersurvey.network.FarmerSurveyApi
import com.sogoamobile.farmersurvey.repository.FarmerSurveyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class FarmerSurveyApiStatus { LOADING, ERROR, DONE }

class SurveyViewModel(private val surveyDao: FarmerSurveyDao) : ViewModel() {

    private val _status = MutableLiveData<FarmerSurveyApiStatus>()
    private val _survey = MutableLiveData<FarmerSurvey>()

    val survey: LiveData<FarmerSurvey> = _survey

    val readQuestions: LiveData<List<QuestionsTable>>
    val readOptions: LiveData<List<OptionsTable>>

    private var _index = MutableLiveData<Int>(0)
    val index: LiveData<Int> = _index

    private var _isFirstTimeLogin = MutableLiveData<Boolean>(true)
    val isFirstTimeLogin: LiveData<Boolean> = _isFirstTimeLogin

    private val farmerSurveyRepository = FarmerSurveyRepository(surveyDao)
    var id = ""

    init {
        readQuestions = farmerSurveyRepository.readQuestions.asLiveData()
        readOptions = farmerSurveyRepository.readOptions.asLiveData()
        if(readQuestions.value != null){
             id = readQuestions.value!![index.value!!].id
        }
    }

    fun updateFirstTimeLogin(value: Boolean){
        _isFirstTimeLogin.value = value
    }

     fun getSurveyFromInternet() {
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

    /**
     * Refresh data from the repository. Using a coroutine launch to run in a
     * background thread.
     */

    // insert to database
    fun addSurvey(surveyTable: FarmerSurveyTable){
        viewModelScope.launch {
            farmerSurveyRepository.addSurvey(surveyTable)
        }
    }

    fun addQuestion(questionsTable: QuestionsTable){
        viewModelScope.launch(Dispatchers.IO) {
            farmerSurveyRepository.addQuestion(questionsTable)
        }
    }

    fun addStrings(stringsTable: StringsTable){
        viewModelScope.launch(Dispatchers.IO) {
            farmerSurveyRepository.addStrings(stringsTable)
        }
    }

    fun addOptions(optionsTable: OptionsTable){
        viewModelScope.launch(Dispatchers.IO) {
            farmerSurveyRepository.addOptions(optionsTable)
        }
    }

    private fun insertQuestions() {
        for(questions in _survey.value!!.questions){
            viewModelScope.launch {
                surveyDao.insertToQuestions(QuestionsTable(questions.id, questions.question_type, questions.question_text))
            }
        }
    }

    // get data from database

    fun retrieveString(id: String): LiveData<StringsTable> {
        return surveyDao.getStrings(id).asLiveData()
    }

    fun retrieveQuestions(): LiveData<List<QuestionsTable>> {
        return surveyDao.getQuestions().asLiveData()
    }

    fun retrieveOptions(): LiveData<List<OptionsTable>> {
        return surveyDao.getOptions().asLiveData()
    }
}

class SurveyViewModelFactory(private val farmerSurveyDao: FarmerSurveyDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SurveyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SurveyViewModel(farmerSurveyDao) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}