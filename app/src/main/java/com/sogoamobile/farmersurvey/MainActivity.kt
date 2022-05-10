package com.sogoamobile.farmersurvey

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import com.sogoamobile.farmersurvey.databinding.ActivityLoginBinding
import com.sogoamobile.farmersurvey.viewmodel.LoginViewModel
import com.sogoamobile.farmersurvey.viewmodel.LoginViewModelFactory

class MainActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels{
        LoginViewModelFactory(
            (application as FarmerSurveyApplication).database
                .surveyDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.log_out -> {
                viewModel.updateIsLoggedIn(false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}