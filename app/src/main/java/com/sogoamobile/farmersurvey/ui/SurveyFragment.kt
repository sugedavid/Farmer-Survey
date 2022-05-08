package com.sogoamobile.farmersurvey.ui

import android.content.ActivityNotFoundException
import android.content.Intent
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
import androidx.fragment.app.activityViewModels
import com.sogoamobile.farmersurvey.FarmerSurveyApplication
import com.sogoamobile.farmersurvey.R
import com.sogoamobile.farmersurvey.database.FarmerSurveyTable
import com.sogoamobile.farmersurvey.database.OptionsTable
import com.sogoamobile.farmersurvey.database.QuestionsTable
import com.sogoamobile.farmersurvey.database.StringsTable
import com.sogoamobile.farmersurvey.databinding.ActivityLoginBinding
import com.sogoamobile.farmersurvey.databinding.FragmentSurveyBinding
import com.sogoamobile.farmersurvey.network.Options
import com.sogoamobile.farmersurvey.network.Question
import com.sogoamobile.farmersurvey.viewmodel.SurveyViewModel
import com.sogoamobile.farmersurvey.viewmodel.SurveyViewModelFactory


class SurveyFragment : Fragment() {

    private val sharedViewModel: SurveyViewModel by activityViewModels {
        SurveyViewModelFactory(
            (activity?.application as FarmerSurveyApplication).database
                .surveyDao()
        )
    }

    private var binding: FragmentSurveyBinding? = null

    var question = ""
    var questionId = ""
    var index = 0
    var questions = listOf<QuestionsTable>()

    val REQUEST_IMAGE_CAPTURE = 1

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

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            surveyFragment = this@SurveyFragment
        }

        sharedViewModel.index.observe(viewLifecycleOwner) { item ->
            index = item
            updateQuestion()
        }

        // next
        binding?.btnNext?.setOnClickListener {
            if (index == questions.size - 1) {
                dispatchTakePictureIntent()
            }
            sharedViewModel.incrementIndex(questions.size - 1)
        }
        // previous
        binding?.btnPrevious?.setOnClickListener {
            sharedViewModel.decrementIndex()
        }

        Toast.makeText(requireContext(), sharedViewModel.isFirstTimeLogin.value.toString(), Toast.LENGTH_LONG).show()
        updateQuestion()

    }

    fun updateQuestion() {
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

                survey.strings["en"]?.forEach { item ->
                    sharedViewModel.addStrings(
                        StringsTable(item.key, item.value)
                    )
                }

                for (option in survey.questions[1].options) {
                    Log.d("options", option.toString())
                    sharedViewModel.addOptions(
                        OptionsTable(option.value, option.display_text)
                    )
                }

                sharedViewModel.updateFirstTimeLogin(false)
                updateQuestion()

            }
        } else {
            sharedViewModel.readQuestions.observe(viewLifecycleOwner) { item ->
                if (item.isNotEmpty()) {
                    Log.d("Survey", item[index].toString())
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
                    }
                    // update previous button
                    if (index == 0) {
                        binding?.btnPrevious?.visibility = View.INVISIBLE
                    } else {
                        binding?.btnPrevious?.visibility = View.VISIBLE
                    }

                    // update question type visibility
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
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // write code to perform some action
                    }
                }
            }
        }

    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

}