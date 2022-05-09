package com.sogoamobile.farmersurvey.viewmodel


import android.app.Application
import android.os.CountDownTimer
import android.widget.Toast
import androidx.lifecycle.*
import androidx.work.*
import com.sogoamobile.farmersurvey.FarmerSurveyApplication
import com.sogoamobile.farmersurvey.R
import com.sogoamobile.farmersurvey.database.*
import com.sogoamobile.farmersurvey.network.FarmerSurvey
import com.sogoamobile.farmersurvey.network.FarmerSurveyApi
import com.sogoamobile.farmersurvey.repository.FarmerSurveyRepository
import com.sogoamobile.farmersurvey.worker.SubmitResponseWorker
import com.sogoamobile.farmersurvey.worker.makeStatusNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class FarmerSurveyApiStatus { LOADING, ERROR, DONE }

class SurveyViewModel(private val application: FarmerSurveyApplication) : ViewModel() {

    private val _status = MutableLiveData<FarmerSurveyApiStatus>()
    private val _survey = MutableLiveData<FarmerSurvey>()
    val survey: LiveData<FarmerSurvey> = _survey

    val readQuestions: LiveData<List<QuestionsTable>>
    val readOptions: LiveData<List<OptionsTable>>

    private var _index = MutableLiveData<Int>(0)
    val index: LiveData<Int> = _index

    private var _isFirstTimeLogin = MutableLiveData<Boolean>(true)
    val isFirstTimeLogin: LiveData<Boolean> = _isFirstTimeLogin

    private val farmerSurveyRepository = FarmerSurveyRepository(application.database.surveyDao())
    var id = ""

    // workManager
    private val workManager = WorkManager.getInstance(application)

    init {
        readQuestions = farmerSurveyRepository.readQuestions.asLiveData()
        readOptions = farmerSurveyRepository.readOptions.asLiveData()
        if(readQuestions.value != null){
             id = readQuestions.value!![index.value!!].id
        }
    }

    // function that counts down every 15 minutes & runs work request
    // to submit survey response & shows notification once work is done

    fun countDownTimer(){
        submitResponseWorker()
        val appContext = application.applicationContext
        val timer = object: CountDownTimer(900000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {

                makeStatusNotification(appContext.getString(R.string.survey_submitted), appContext)
                countDownTimer()
            }
        }
        timer.start()
    }

    private fun submitResponseWorker() {

        // internet connection constraint
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<SubmitResponseWorker>()
                .setConstraints(constraints)
                .build()
        workManager
            .enqueue(uploadWorkRequest)

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

    fun resetIndex(){
        _index.value = 0
    }

    /**
     * Refresh data from the repository. Using a coroutine launch to run in a
     * background thread.
     */

    // insert to database using repo
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

    fun addSurveyResponse(surveyResponseTable: SurveyResponseTable){
        viewModelScope.launch(Dispatchers.IO) {
            farmerSurveyRepository.addSurveyResponse(surveyResponseTable)
        }
    }

    // get data from database using repo

    fun retrieveString(id: String): LiveData<StringsTable> {
        return application.database.surveyDao().getStrings(id).asLiveData()
    }

    fun retrieveQuestions(): LiveData<List<QuestionsTable>> {
        return application.database.surveyDao().getQuestions().asLiveData()
    }

    fun retrieveOptions(): LiveData<List<OptionsTable>> {
        return application.database.surveyDao().getOptions().asLiveData()
    }
}

class SurveyViewModelFactory(private val application: FarmerSurveyApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SurveyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SurveyViewModel(application) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}