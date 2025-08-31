package rma.tjaksic.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import rma.tjaksic.R
import rma.tjaksic.data.Appointment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.recyclerview.widget.ListAdapter

class AppointmentAdapter(
    private val onItemClick: (Appointment) -> Unit,
    private val onEditClick: (Appointment) -> Unit,
    private val onDeleteClick: (Appointment) -> Unit
) : ListAdapter<Appointment, AppointmentAdapter.AppointmentViewHolder>(AppointmentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val customerNameTextView: TextView = itemView.findViewById(R.id.tv_customer_name)
        private val phoneTextView: TextView = itemView.findViewById(R.id.tv_phone)
        private val serviceTextView: TextView = itemView.findViewById(R.id.tv_service)
        private val dateTextView: TextView = itemView.findViewById(R.id.tv_date)
        private val photoImageView: ImageView = itemView.findViewById(R.id.iv_photo)

        private val photoCard: androidx.cardview.widget.CardView = itemView.findViewById(R.id.photo_card)
        private val editButton: ImageView = itemView.findViewById(R.id.btn_edit)
        private val deleteButton: ImageView = itemView.findViewById(R.id.btn_delete)

        fun bind(appointment: Appointment) {
            customerNameTextView.text = appointment.customerName
            phoneTextView.text = formatPhoneNumber(appointment.phoneNumber)
            serviceTextView.text = appointment.serviceType

            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            dateTextView.text = dateFormat.format(appointment.appointmentDate)

            if (!appointment.photoPath.isNullOrEmpty()) {
                val file = File(appointment.photoPath)
                if (file.exists()) {
                    try {
                        photoImageView.setImageURI(Uri.fromFile(file))
                        photoCard.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        photoCard.visibility = View.GONE
                        e.printStackTrace()
                    }
                } else {
                    photoCard.visibility = View.GONE
                }
            } else {
                photoCard.visibility = View.GONE
            }

            itemView.setOnClickListener { onItemClick(appointment) }
            editButton.setOnClickListener { onEditClick(appointment) }
            deleteButton.setOnClickListener { onDeleteClick(appointment) }
        }
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        val digitsOnly = phoneNumber.replace(Regex("[^\\d]"), "")

        return when {
            digitsOnly.length == 11 && digitsOnly.startsWith("385") -> {
                val nationalNumber = digitsOnly.substring(3)
                when {
                    nationalNumber.startsWith("9") -> "+385 ${nationalNumber.substring(0, 2)} ${nationalNumber.substring(2, 5)} ${nationalNumber.substring(5)}" // Mobile
                    else -> "+385 ${nationalNumber.substring(0, 2)} ${nationalNumber.substring(2, 5)} ${nationalNumber.substring(5)}"
                }
            }
            digitsOnly.length == 9 && digitsOnly.startsWith("9") -> {
                "+385 ${digitsOnly.substring(0, 2)} ${digitsOnly.substring(2, 5)} ${digitsOnly.substring(5)}"
            }
            digitsOnly.length == 8 -> {
                "+385 ${digitsOnly.substring(0, 2)} ${digitsOnly.substring(2, 5)} ${digitsOnly.substring(5)}"
            }
            digitsOnly.length >= 10 -> {
                if (digitsOnly.startsWith("1") && digitsOnly.length == 11) {
                    "+1 (${digitsOnly.substring(1, 4)}) ${digitsOnly.substring(4, 7)}-${digitsOnly.substring(7)}"
                } else {
                    "+${digitsOnly.substring(0, digitsOnly.length - 9)} ${digitsOnly.substring(digitsOnly.length - 9, digitsOnly.length - 6)} ${digitsOnly.substring(digitsOnly.length - 6, digitsOnly.length - 3)} ${digitsOnly.substring(digitsOnly.length - 3)}"
                }
            }
            else -> {
                digitsOnly.chunked(3).joinToString(" ")
            }
        }
    }

    class AppointmentDiffCallback : DiffUtil.ItemCallback<Appointment>() {
        override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
            return oldItem == newItem
        }
    }
}