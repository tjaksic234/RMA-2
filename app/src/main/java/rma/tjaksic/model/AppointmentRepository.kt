package rma.tjaksic.model

import androidx.lifecycle.LiveData

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