package com.sogoamobile.farmersurvey

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.sogoamobile.farmersurvey.databinding.ActivityLoginBinding
import com.sogoamobile.farmersurvey.viewmodel.LoginViewModel
import com.sogoamobile.farmersurvey.viewmodel.SurveyViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: SurveyViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}