package com.example.pomodoro

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment

class SettingsDialog : DialogFragment() {
    private var onDismissListener: ((String, String) -> Unit)? = null
    private lateinit var timeFocusEditText: EditText
    private lateinit var timeRestEditText: EditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timeFocusEditText = view.findViewById(R.id.timeFocusEditText)
        timeRestEditText = view.findViewById(R.id.timeRestEditText)
        val saveButton: Button = view.findViewById(R.id.saveButton)

        timeFocusEditText.setOnClickListener {
            showTimePickerDialog(timeFocusEditText)
        }

        timeRestEditText.setOnClickListener {
            showTimePickerDialog(timeRestEditText)
        }

        saveButton.setOnClickListener {
            val timeFocus = timeFocusEditText.text.toString()
            val timeRest = timeRestEditText.text.toString()

            if(!validateData(timeFocus, timeRest)){
                return@setOnClickListener
            }

            val sharedPreferences = requireActivity().getSharedPreferences("PomodoroPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("timeFocus", timeFocus)
            editor.putString("timeRest", timeRest)
            editor.apply()
            dismiss()
        }
    }

    private fun validateData(timeFocus: String, timeRest: String): Boolean {
        var isValid = true
        if (timeFocus.isBlank()) {
            timeFocusEditText.error = "Por favor, insira o tempo de foco."
            isValid = false
        }

        if (timeRest.isBlank()) {
            timeRestEditText.error = "Por favor, insira o tempo de descanso."
            isValid = false
        }
        return isValid
    }

    fun setOnDismissListener(listener: (String, String) -> Unit) {
        onDismissListener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        val timeFocus = view?.findViewById<EditText>(R.id.timeFocusEditText)?.text.toString()
        val timeRest = view?.findViewById<EditText>(R.id.timeRestEditText)?.text.toString()

        onDismissListener?.invoke(timeFocus, timeRest)
    }

    override fun onResume() {
        super.onResume()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(width, height)
    }

    private fun showTimePickerDialog(editText: EditText) {
        val minutePicker = NumberPicker(requireContext())
        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.value = 0

        val secondPicker = NumberPicker(requireContext())
        secondPicker.minValue = 0
        secondPicker.maxValue = 59
        secondPicker.value = 0

        val dialogLayout = LinearLayout(requireContext())
        dialogLayout.orientation = LinearLayout.HORIZONTAL
        dialogLayout.gravity = Gravity.CENTER
        dialogLayout.addView(minutePicker)
        dialogLayout.addView(secondPicker)

        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Selecione o tempo")
            .setView(dialogLayout)
            .setPositiveButton("OK") { _, _ ->
                val time = String.format("%02d:%02d", minutePicker.value, secondPicker.value)
                editText.setText(time)
            }
            .setNegativeButton("Cancelar", null)

        builder.create().show()
    }

}
