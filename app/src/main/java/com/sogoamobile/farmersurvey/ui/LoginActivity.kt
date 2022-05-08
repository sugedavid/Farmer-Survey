package com.sogoamobile.farmersurvey.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.sogoamobile.farmersurvey.FarmerSurveyApplication
import com.sogoamobile.farmersurvey.MainActivity
import com.sogoamobile.farmersurvey.R
import com.sogoamobile.farmersurvey.databinding.ActivityLoginBinding
import com.sogoamobile.farmersurvey.viewmodel.LoginViewModel
import com.sogoamobile.farmersurvey.viewmodel.LoginViewModelFactory
import com.sogoamobile.farmersurvey.viewmodel.SurveyViewModelFactory

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels{
        LoginViewModelFactory(
            (application as FarmerSurveyApplication).database
                .surveyDao()
        )
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root

        viewModel.isLoggedIn.observe(this,){item ->
            // check if user is logged in & navigate to home page
            if(item?.isLoggedIn == true){
                startActivity(  Intent(this, MainActivity::class.java))
            }else{
                setContentView(view)
            }
        }

        binding.login.setOnClickListener{
            // validate login credentials
            if(viewModel.isPasswordValid(binding.password.text.toString())
                && viewModel.isPhoneNumberValid(binding.phoneNumber.text.toString())){
                // navigate to home page
                viewModel.updateIsLoggedIn(true)
                startActivity(  Intent(this, MainActivity::class.java))
            }
            else if (!viewModel.isPhoneNumberValid(binding.phoneNumber.text.toString())){
                Toast.makeText(this, R.string.invalid_phone_number, Toast.LENGTH_SHORT).show()
            }
            else if (!viewModel.isPasswordValid(binding.password.text.toString())){
                Toast.makeText(this, R.string.invalid_password, Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, R.string.invalid_credentials, Toast.LENGTH_SHORT).show()
            }
        }

    }
}