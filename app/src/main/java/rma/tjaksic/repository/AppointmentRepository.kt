package rma.tjaksic.repository

import androidx.lifecycle.LiveData
import rma.tjaksic.data.Appointment
import rma.tjaksic.data.AppointmentDao

class AppointmentRepository(private val appointmentDao: AppointmentDao) {

    val allAppointments: LiveData<List<Appointment>> = appointmentDao.getAllAppointments()

    suspend fun insert(appointment: Appointment): Long {
        return appointmentDao.insertAppointment(appointment)
    }

    suspend fun update(appointment: Appointment) {
        appointmentDao.updateAppointment(appointment)
    }

    suspend fun delete(appointment: Appointment) {
        appointmentDao.deleteAppointment(appointment)
    }

    suspend fun getAppointmentById(id: Long): Appointment? {
        return appointmentDao.getAppointmentById(id)
    }
}