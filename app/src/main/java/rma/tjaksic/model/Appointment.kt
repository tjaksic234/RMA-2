package rma.tjaksic.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerName: String,
    val phoneNumber: String,
    val serviceType: String,
    val appointmentDate: Date,
    val photoPath: String? = null
)