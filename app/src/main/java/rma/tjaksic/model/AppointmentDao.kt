package rma.tjaksic.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY appointmentDate DESC")
    fun getAllAppointments(): LiveData<List<Appointment>>

    @Insert
    suspend fun insertAppointment(appointment: Appointment): Long

    @Update
    suspend fun updateAppointment(appointment: Appointment)

    @Delete
    suspend fun deleteAppointment(appointment: Appointment)

    @Query("SELECT * FROM appointments WHERE id = :id")
    suspend fun getAppointmentById(id: Long): Appointment?
}