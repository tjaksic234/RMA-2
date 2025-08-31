package rma.tjaksic

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import rma.tjaksic.data.Appointment
import rma.tjaksic.ui.adapter.AppointmentAdapter
import rma.tjaksic.ui.viewmodel.AppointmentViewModel

class MainActivity : AppCompatActivity() {

    private val appointmentViewModel: AppointmentViewModel by viewModels()
    private lateinit var adapter: AppointmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()
        setupFab()
        observeAppointments()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        adapter = AppointmentAdapter(
            onItemClick = { appointment ->
                val intent = Intent(this, AppointmentDetailActivity::class.java)
                intent.putExtra("appointment_id", appointment.id)
                startActivity(intent)
            },
            onEditClick = { appointment ->
                val intent = Intent(this, AddEditAppointmentActivity::class.java)
                intent.putExtra("appointment_id", appointment.id)
                startActivity(intent)
            },
            onDeleteClick = { appointment ->
                showDeleteConfirmationDialog(appointment)
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupFab() {
        val fab = findViewById<FloatingActionButton>(R.id.fab_add)
        fab.setOnClickListener {
            val intent = Intent(this, AddEditAppointmentActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeAppointments() {
        appointmentViewModel.allAppointments.observe(this) { appointments ->
            adapter.submitList(appointments)
        }
    }

    private fun showDeleteConfirmationDialog(appointment: Appointment) {
        AlertDialog.Builder(this)
            .setTitle("Obriši termin")
            .setMessage("Jeste li sigurni da želite obrisati ovaj termin?")
            .setPositiveButton("Da") { _, _ ->
                appointmentViewModel.delete(appointment)
            }
            .setNegativeButton("Ne", null)
            .show()
    }
}