// MainActivity.kt
package com.example.epiapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.epiapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), EpiAdapter.OnItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var epiAdapter: EpiAdapter
    private var epiList: List<Epi> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        loadEpis()

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddEditEpiActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadEpis()
    }

    private fun loadEpis() {
        epiList = dbHelper.getAllEpis()
        epiAdapter = EpiAdapter(this, epiList, this)
        binding.recyclerView.adapter = epiAdapter
        checkLowStock()
    }

    override fun onEditClick(epi: Epi) {
        val intent = Intent(this, AddEditEpiActivity::class.java)
        intent.putExtra("EPI_ID", epi.id)
        startActivity(intent)
    }

    override fun onDeleteClick(epi: Epi) {
        AlertDialog.Builder(this)
            .setTitle("Excluir EPI")
            .setMessage("Tem certeza de que deseja excluir '${epi.nome}'?")
            .setPositiveButton("Sim") { dialog, _ ->
                val success = dbHelper.deleteEpi(epi.id)
                if (success) {
                    Toast.makeText(this, "EPI excluído com sucesso!", Toast.LENGTH_SHORT).show()
                    loadEpis()
                } else {
                    Toast.makeText(this, "Falha ao excluir EPI.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Não") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun checkLowStock() {
        val lowStockEpis = epiList.filter { it.quantidade < 5 }
        if (lowStockEpis.isNotEmpty()) {
            val message = StringBuilder("EPIs com baixo estoque:\n")
            lowStockEpis.forEach { message.append("- ${it.nome}: ${it.quantidade}\n") }

            AlertDialog.Builder(this)
                .setTitle("Alerta de Baixo Estoque")
                .setMessage(message.toString())
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }
}

// Adicionar importações
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

// Dentro da classe MainActivity
private val CHANNEL_ID = "low_stock_channel"
private val NOTIFICATION_ID = 1

override fun onCreate(savedInstanceState: Bundle?) {

    createNotificationChannel()
}

private fun checkLowStock() {
    val lowStockEpis = epiList.filter { it.quantidade < 5 }
    if (lowStockEpis.isNotEmpty()) {
        val message = StringBuilder("EPIs com baixo estoque:\n")
        lowStockEpis.forEach { message.append("- ${it.nome}: ${it.quantidade}\n") }

        // Enviar notificação
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Alerta de Baixo Estoque")
            .setContentText("Verifique os EPIs com baixa quantidade.")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message.toString()))
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }
}

private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Canal de Baixo Estoque"
        val descriptionText = "Notificações para EPIs com estoque baixo"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Registrar o canal com o sistema
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
