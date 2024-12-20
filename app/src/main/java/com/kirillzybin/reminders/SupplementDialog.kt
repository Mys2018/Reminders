package com.kirillzybin.reminders

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SupplementDialog : DialogFragment() {
    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = layoutInflater.inflate(R.layout.supplement, null)

        val name = view.findViewById<EditText>(R.id.name)
        val description = view.findViewById<EditText>(R.id.description)

        var timeSel = "0:00"
        var dateSel = "01.01.2100"

        val dateTime = view.findViewById<TextView>(R.id.time_and_date)

        val importanceGroup = view.findViewById<RadioGroup>(R.id.group_importance)
        val timeButton = view.findViewById<Button>(R.id.time_button)

        timeButton.setOnClickListener {
            showDatePickerDialog { selectedDate ->
                showTimePickerDialog { selectedTime ->
                    timeSel = selectedTime
                    dateSel = selectedDate
                    dateTime.text = "Выбрано: $selectedDate $selectedTime"
                }
            }
        }

        var importance = -1

        importanceGroup.setOnCheckedChangeListener { _, checkedId ->
            importance = when (checkedId) {
                R.id.Importance_0 -> 0
                R.id.Importance_1 -> 1
                R.id.Importance_2 -> 2
                else -> 2
            }
        }
        return AlertDialog.Builder(requireContext())
            .setTitle("Добавление")
            .setView(view)
            .setPositiveButton("Добавить") { _, _ ->
                val nameOut = if (name.text.toString() != "") name.text.toString() else ("Напоминание от ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.mm.dd hh:mm"))}")
                val descriptionOut = if(description.text.toString() != "") description.text.toString() else ("Описание отсутствует")
                val importanceOut = importance
                val timeOut = timeSel
                val dateOut = dateSel

                listener.onDialogData(nameOut, descriptionOut, importanceOut, timeOut, dateOut)
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .create()
    }

    interface DataListener {

        fun onDialogData(
            name: String,
            description: String,
            importance: Int,
            time: String,
            date: String
        )
    }

    private lateinit var listener: DataListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as DataListener
    }

    @SuppressLint("DefaultLocale")
    private fun showDatePickerDialog(onDateSelected: (String) -> Unit){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, y, m, d ->
            val date = String.format("%02d.%02d.%d", d, m + 1, y)
            onDateSelected(date)
        }, year, month, day)

        datePickerDialog.show()
    }


    @SuppressLint("DefaultLocale")
    private fun showTimePickerDialog(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), { _, h, m ->
            val time = String.format("%02d:%02d", h, m)
            onTimeSelected(time)
        }, hour, minute, true)

        timePickerDialog.show()
    }
}