package com.sogoamobile.farmersurvey.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sogoamobile.farmersurvey.FarmerSurveyApplication
import com.sogoamobile.farmersurvey.MainActivity
import com.sogoamobile.farmersurvey.R
import com.sogoamobile.farmersurvey.database.*
import com.sogoamobile.farmersurvey.databinding.ActivityLoginBinding
import com.sogoamobile.farmersurvey.databinding.FragmentSurveyBinding
import com.sogoamobile.farmersurvey.network.Options
import com.sogoamobile.farmersurvey.network.Question
import com.sogoamobile.farmersurvey.viewmodel.LoginViewModel
import com.sogoamobile.farmersurvey.viewmodel.LoginViewModelFactory
import com.sogoamobile.farmersurvey.viewmodel.SurveyViewModel
import com.sogoamobile.farmersurvey.viewmodel.SurveyViewModelFactory
import java.io.FileDescriptor
import java.io.IOException


class SurveyFragment : Fragment() {

    private val sharedViewModel: SurveyViewModel by activityViewModels {
        SurveyViewModelFactory(
            activity?.application as FarmerSurveyApplication
        )
    }

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            (activity?.application as FarmerSurveyApplication).database
                .surveyDao()
        )
    }

    private var binding: FragmentSurveyBinding? = null

    var question = ""
    var questionId = ""
    var index = 0
    var questions = listOf<QuestionsTable>()
    var questionType = ""
    var spGender = ""
    private val REQUEST_IMAGE_CAPTURE = 1
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentSurveyBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        // Inflate the layout for this fragment
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isLoggedIn.observe(viewLifecycleOwner) { item ->
            // check if user is logged in & navigate to Log in page
            if (!item.isLoggedIn) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
        }

        sharedViewModel.countDownTimer()

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            surveyFragment = this@SurveyFragment
        }

        sharedViewModel.index.observe(viewLifecycleOwner) { item ->
            index = item
            updateQuestion()
        }

        // next btn
        binding?.btnNext?.setOnClickListener {
            if (index == questions.size - 1) {
                dispatchTakePictureIntent()
            }
            saveResponse()
            sharedViewModel.incrementIndex(questions.size - 1)
        }
        // previous btn
        binding?.btnPrevious?.setOnClickListener {
            sharedViewModel.decrementIndex()
        }
        // finish btn
        binding?.btnFinish?.setOnClickListener {
            saveResponse()
            // show alert dialog
            sharedViewModel.resetIndex()
            alertDialog()

        }
        updateQuestion()

        // options
        sharedViewModel.survey.observe(viewLifecycleOwner) { survey ->
            var indexTemp = 0
            while(survey.questions[indexTemp].options.isEmpty()){
                indexTemp += 1
            }
            for (option in survey.questions[indexTemp].options) {
                sharedViewModel.addOptions(
                    OptionsTable(option.value, option.display_text)
                )
            }
        }

    }

    private fun updateQuestion() {
        // check if there's data in the db
        if (sharedViewModel.isFirstTimeLogin.value == true) {
            sharedViewModel.getSurveyFromInternet()
            sharedViewModel.survey.observe(viewLifecycleOwner) { survey ->

                // save to database
                for (question in survey.questions) {
                    sharedViewModel.addQuestion(
                        QuestionsTable(
                            question.id,
                            question.question_type,
                            question.question_text
                        )
                    )
                }

                // strings
                survey.strings["en"]?.forEach { item ->
                    sharedViewModel.addStrings(
                        StringsTable(item.key, item.value)
                    )
                }

                sharedViewModel.updateFirstTimeLogin(false)
                updateQuestion()

            }
        } else {
            sharedViewModel.readQuestions.observe(viewLifecycleOwner) { item ->
                if (item.isNotEmpty()) {
                    questions = item
                    questionId = questions[index].id
                    // strings
                    sharedViewModel.retrieveString(questionId).observe(viewLifecycleOwner) { item ->
                        question = item?.en ?: ""
                        // update question
                        binding?.txtQuestion?.text = "${index + 1}. $question"
                    }
                    // update next button
                    if (index == questions.size - 1) {
                        binding?.btnNext?.text = getString(R.string.take_photo)

                    } else {
                        binding?.btnNext?.text = getString(R.string.next)
                        binding?.btnFinish?.visibility = View.GONE
                    }
                    // update previous button
                    if (index == 0) {
                        binding?.btnPrevious?.visibility = View.INVISIBLE
                    } else {
                        binding?.btnPrevious?.visibility = View.VISIBLE
                    }

                    // update question type visibility
                    questionType = questions[index].question_type
                    when (questions[index].question_type) {
                        "FREE_TEXT" -> {
                            binding?.edtSingleLineText?.visibility = View.VISIBLE
                            binding?.edtTypeValue?.visibility = View.GONE
                            binding?.spnGender?.visibility = View.GONE
                        }
                        "SELECT_ONE" -> {
                            binding?.edtSingleLineText?.visibility = View.GONE
                            binding?.edtTypeValue?.visibility = View.GONE
                            binding?.spnGender?.visibility = View.VISIBLE
                        }
                        "TYPE_VALUE" -> {
                            binding?.edtSingleLineText?.visibility = View.GONE
                            binding?.edtTypeValue?.visibility = View.VISIBLE
                            binding?.spnGender?.visibility = View.GONE
                        }
                        else -> {
                            binding?.edtSingleLineText?.visibility = View.GONE
                            binding?.edtTypeValue?.visibility = View.GONE
                            binding?.spnGender?.visibility = View.GONE
                        }
                    }
                }
            }

            // options
            sharedViewModel.readOptions.observe(viewLifecycleOwner) { item ->
                //Spinner
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item, item
                )
                binding?.spnGender?.adapter = adapter
                binding?.spnGender?.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View, position: Int, id: Long
                    ) {
                        binding?.spnGender?.setSelection(position)
                        spGender = binding?.spnGender?.selectedItem.toString()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                    }
                }
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, getString(R.string.photo_title))
        values.put(MediaStore.Images.Media.DESCRIPTION, getString(R.string.photo_desc))
        imageUri =
            activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
    }

    private fun saveResponse() {
        var answer = ""
        when (questionType) {
            "FREE_TEXT" -> {
                answer = binding?.edtSingleLineText?.text.toString()
            }
            "SELECT_ONE" -> {
                answer = binding?.spnGender?.selectedItem.toString()
            }
            "TYPE_VALUE" -> {
                answer = binding?.edtTypeValue?.text.toString()
            }
            else -> {
                binding?.edtSingleLineText?.visibility = View.GONE
                binding?.edtTypeValue?.visibility = View.GONE
                binding?.spnGender?.visibility = View.GONE
            }
        }
        sharedViewModel.addSurveyResponse(SurveyResponseTable(questionId, answer))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val bitmap = uriToBitmap(imageUri!!)
            sharedViewModel.addSurveyResponse(
                SurveyResponseTable(
                    "q_take_photo",
                    bitmap.toString()
                )
            )
            binding?.btnFinish?.visibility = View.VISIBLE
        }
    }

    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor =
                activity?.contentResolver?.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun alertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.survey_completed))
        builder.setMessage(getString(R.string.survey_completed_desc))
        builder.setPositiveButton(getString(R.string.okay)) { _, _ ->
        }
        builder.show()
    }

}