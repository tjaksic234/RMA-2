package rma.tjaksic

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import rma.tjaksic.ui.viewmodel.AppointmentViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class AppointmentDetailActivity : AppCompatActivity() {

    private val appointmentViewModel: AppointmentViewModel by viewModels()

    private lateinit var tvCustomerName: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvService: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvPhotoLabel: TextView
    private lateinit var ivPhoto: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detalji termina"

        initViews()

        val appointmentId = intent.getLongExtra("appointment_id", -1L)
        if (appointmentId != -1L) {
            loadAppointmentDetails(appointmentId)
        }
    }

    private fun initViews() {
        tvCustomerName = findViewById(R.id.tv_customer_name)
        tvPhone = findViewById(R.id.tv_phone)
        tvService = findViewById(R.id.tv_service)
        tvDate = findViewById(R.id.tv_date)
        tvPhotoLabel = findViewById(R.id.tv_photo_label)
        ivPhoto = findViewById(R.id.iv_photo)
    }

    @SuppressLint("SetTextI18n")
    private fun loadAppointmentDetails(appointmentId: Long) {
        lifecycleScope.launch {
            val appointment = appointmentViewModel.getAppointmentById(appointmentId)
            appointment?.let {
                tvCustomerName.text = "${it.customerName}"
                tvPhone.text = "${it.phoneNumber}"
                tvService.text = "${it.serviceType}"

                val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                tvDate.text = "${dateFormat.format(it.appointmentDate)}"

                if (!it.photoPath.isNullOrEmpty()) {
                    val file = File(it.photoPath)
                    if (file.exists()) {
                        ivPhoto.setImageURI(Uri.fromFile(file))
                        ivPhoto.visibility = View.VISIBLE
                        tvPhotoLabel.visibility = View.VISIBLE
                    } else {
                        ivPhoto.visibility = View.GONE
                        tvPhotoLabel.visibility = View.GONE
                    }
                } else {
                    ivPhoto.visibility = View.GONE
                    tvPhotoLabel.visibility = View.GONE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}