package com.example.prakt_19_1

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings.System.DATE_FORMAT
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import java.util.*

class CrimeFragment: Fragment() {
    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var chooseButton: Button
    private lateinit var sendCrimeButton: Button
    private var  REQUEST_CONTACT=1
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var button_AddCrime: Button
    override fun onCreate(savedInstranceState: Bundle?) {
        super.onCreate(savedInstranceState)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        chooseButton = view.findViewById(R.id.button_chooseSuspect)
        sendCrimeButton = view.findViewById(R.id.button_sendCrime)
        button_AddCrime = view.findViewById(R.id.button_AddCrime)
        dateButton.apply()
        {
            isEnabled = false
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val hours = calendar.get(Calendar.HOUR_OF_DAY)
            val minutes = calendar.get(Calendar.MINUTE)
            dateButton.text = "$day.$month.$year $hours:$minutes"
        }
        return view

    }

    override fun onStart() {
        super.onStart()
        val titleWatcher = object :
            TextWatcher {
            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int, count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int, before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(sequence: Editable?) {

            }
        }
        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    dateButton.isEnabled = true
                    chooseButton.isEnabled = true
                    sendCrimeButton.isEnabled = true
                    button_AddCrime.isEnabled = true
                } else {
                    dateButton.isEnabled = false
                    button_AddCrime.isEnabled = false
                    chooseButton.isEnabled = false
                    sendCrimeButton.isEnabled = false
                }
            }
        }

        chooseButton.apply{
            val pickContactIntent=Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            setOnClickListener {
                startActivityForResult(pickContactIntent, 1)
            }
            val packageManager: PackageManager =requireActivity().packageManager
            val resolvedActivity: ResolveInfo?=packageManager.resolveActivity(pickContactIntent, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity==null){
                isEnabled=false
            }
        }

        sendCrimeButton.setOnClickListener(){
            if (titleField.text.isEmpty()){
                Toast.makeText(requireContext(),"Title field is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Intent(Intent.ACTION_SEND).apply{
                type="text/plain"
                putExtra(Intent.EXTRA_TEXT,getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_suspect))
            }.also{ intent ->
                val chooserIntent=Intent.createChooser(intent,getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        button_AddCrime.setOnClickListener()
        {
            var title: String = ""
            var date: String = ""
            var suspend: String = "null"

            if (titleField.text.trim().isNotEmpty()) {
                title = titleField.text.toString()
                date = dateButton.text.toString()
                if (chooseButton.text.trim() != "Choose suspect")
                {
                    suspend = chooseButton.text.toString()
                }

                val dbHelper = DBHelper (requireContext())
                val isStock = dbHelper.getCrime(title)
                if(!isStock) {
                    var crime = Crime(title, date, suspend)
                    val db = DBHelper(requireContext())
                    db.addCrime(crime)
                    titleField.text.clear()
                    chooseButton.text = "Choose suspect"
                    var toast = Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT)
                    toast.show()
                }
                else
                {
                    titleField.text.clear()
                    chooseButton.text = "Choose suspect"
                    var toast = Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
            else
            {
                Toast.makeText(requireContext(),"Title field is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return
            requestCode == REQUEST_CONTACT && data != null -> {
                val contactUri: Uri? = data.data
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                val cursor =
                    contactUri?.let {
                        requireActivity().contentResolver.query(
                            it, queryFields, null,
                            null
                        )
                    }
                cursor?.use {
                    if (it.count == 0) {
                        return
                    }
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    chooseButton.text = suspect
                }

            }
        }
    }
    private fun getCrimeReport(): String {
        var message: String = ""
        message += "${titleField.text.trim()}! "
        message += "The crime was discovered on. "
        message += "${dateButton.text}. "
        message += "The case is solved, and there is no suspend."
        return message
    }
}
