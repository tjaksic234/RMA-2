package rma.tjaksic.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import rma.tjaksic.data.Appointment
import rma.tjaksic.data.AppointmentDatabase
import rma.tjaksic.repository.AppointmentRepository

class AppointmentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppointmentRepository
    val allAppointments: LiveData<List<Appointment>>

    init {
        val appointmentDao = AppointmentDatabase.getDatabase(application).appointmentDao()
        repository = AppointmentRepository(appointmentDao)
        allAppointments = repository.allAppointments
    }

    fun insert(appointment: Appointment) = viewModelScope.launch {
        repository.insert(appointment)
    }

    fun update(appointment: Appointment) = viewModelScope.launch {
        repository.update(appointment)
    }

    fun delete(appointment: Appointment) = viewModelScope.launch {
        repository.delete(appointment)
    }

    suspend fun getAppointmentById(id: Long): Appointment? {
        return repository.getAppointmentById(id)
    }
}