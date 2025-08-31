package rma.tjaksic

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import rma.tjaksic.data.Appointment
import rma.tjaksic.ui.viewmodel.AppointmentViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddEditAppointmentActivity : AppCompatActivity() {

    private val appointmentViewModel: AppointmentViewModel by viewModels()

    private lateinit var etCustomerName: EditText
    private lateinit var etPhone: EditText
    private lateinit var spinnerService: Spinner
    private lateinit var btnSelectDate: Button
    private lateinit var btnTakePhoto: Button
    private lateinit var ivPhoto: ImageView
    private lateinit var btnSave: Button

    private var selectedDate: Date = Date()
    private var photoPath: String? = null
    private var appointmentId: Long = -1L
    private var isEditMode = false
    private var currentPhotoFile: File? = null
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private var tempPhotoUri: Uri? = null

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_appointment)

        initViews()
        setupSpinner()
        setupCameraLauncher()
        setupClickListeners()

        appointmentId = intent.getLongExtra("appointment_id", -1L)
        isEditMode = appointmentId != -1L

        if (isEditMode) {
            title = "Uredi termin"
            loadAppointmentData()
        } else {
            title = "Dodaj termin"
        }
    }

    private fun initViews() {
        etCustomerName = findViewById(R.id.et_customer_name)
        etPhone = findViewById(R.id.et_phone)
        spinnerService = findViewById(R.id.spinner_service)
        btnSelectDate = findViewById(R.id.btn_select_date)
        btnTakePhoto = findViewById(R.id.btn_take_photo)
        ivPhoto = findViewById(R.id.iv_photo)
        btnSave = findViewById(R.id.btn_save)
    }

    private fun setupSpinner() {
        val services = arrayOf("Šišanje", "Brijanje", "Šišanje + Brijanje", "Pranje kose")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, services)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerService.adapter = adapter
    }

    private fun setupCameraLauncher() {
        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                tempPhotoUri?.let { uri ->
                    // Get the actual file path from the FileProvider URI
                    val file = File(filesDir, uri.lastPathSegment ?: "photo_${System.currentTimeMillis()}.jpg")
                    if (file.exists()) {
                        photoPath = file.absolutePath
                        ivPhoto.setImageURI(uri)
                        ivPhoto.visibility = ImageView.VISIBLE

                        Toast.makeText(this, "Slika je snimljena", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Greška pri snimanju slike", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        btnSelectDate.setOnClickListener {
            showDateTimePicker()
        }

        btnTakePhoto.setOnClickListener {
            takePhoto()
        }

        btnSave.setOnClickListener {
            saveAppointment()
        }
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            TimePickerDialog(this, { _, hourOfDay, minute ->
                calendar.set(year, month, dayOfMonth, hourOfDay, minute)
                selectedDate = calendar.time
                updateDateButton()
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateDateButton() {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        btnSelectDate.text = dateFormat.format(selectedDate)
    }

    private fun takePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            launchCamera()
        }
    }

    private fun launchCamera() {
        val photoFileName = "photo_${System.currentTimeMillis()}.jpg"
        val photoFile = File(filesDir, photoFileName)

        currentPhotoFile = photoFile

        tempPhotoUri = FileProvider.getUriForFile(
            this,
            "rma.tjaksic.fileprovider",
            photoFile
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, tempPhotoUri)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        cameraLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera()
                } else {
                    Toast.makeText(
                        this,
                        "Potrebna dozvola za kameru za fotografiranje",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun saveAppointment() {
        val customerName = etCustomerName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val service = spinnerService.selectedItem.toString()

        if (customerName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Molimo unesite sve potrebne podatke", Toast.LENGTH_SHORT).show()
            return
        }

        val finalPhotoPath = currentPhotoFile?.let { file ->
            if (file.exists()) {
                file.absolutePath
            } else {
                photoPath
            }
        } ?: photoPath

        val appointment = if (isEditMode) {
            Appointment(appointmentId, customerName, phone, service, selectedDate, finalPhotoPath)
        } else {
            Appointment(0, customerName, phone, service, selectedDate, finalPhotoPath)
        }

        if (isEditMode) {
            appointmentViewModel.update(appointment)
            Toast.makeText(this, "Termin je ažuriran", Toast.LENGTH_SHORT).show()
        } else {
            appointmentViewModel.insert(appointment)
            Toast.makeText(this, "Termin je dodan", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    private fun loadAppointmentData() {
        lifecycleScope.launch {
            val appointment = appointmentViewModel.getAppointmentById(appointmentId)
            appointment?.let {
                etCustomerName.setText(it.customerName)
                etPhone.setText(it.phoneNumber)

                val services = arrayOf("Šišanje", "Brijanje", "Šišanje + Brijanje", "Pranje kose")
                val serviceIndex = services.indexOf(it.serviceType)
                if (serviceIndex != -1) {
                    spinnerService.setSelection(serviceIndex)
                }

                selectedDate = it.appointmentDate
                updateDateButton()

                if (!it.photoPath.isNullOrEmpty()) {
                    photoPath = it.photoPath
                    val file = File(it.photoPath)
                    if (file.exists()) {
                        ivPhoto.setImageURI(Uri.fromFile(file))
                        ivPhoto.visibility = ImageView.VISIBLE
                    }
                }
            }
        }
    }
}