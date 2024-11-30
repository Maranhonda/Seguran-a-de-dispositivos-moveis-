// AddEditEpiActivity.kt
package com.example.epiapp

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.epiapp.databinding.ActivityAddEditEpiBinding

class AddEditEpiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditEpiBinding
    private lateinit var dbHelper: DatabaseHelper
    private var epiId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditEpiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        if (intent.hasExtra("EPI_ID")) {
            epiId = intent.getIntExtra("EPI_ID", -1)
            if (epiId != -1) {
                loadEpiDetails(epiId)
            }
        }

        binding.btnSave.setOnClickListener {
            saveEpi()
        }
    }

    private fun loadEpiDetails(id: Int) {
        val epis = dbHelper.getAllEpis()
        val epi = epis.find { it.id == id }
        epi?.let {
            binding.etNome.setText(it.nome)
            binding.etQuantidade.setText(it.quantidade.toString())
            binding.etDescricao.setText(it.descricao)
        }
    }

    private fun saveEpi() {
        val nome = binding.etNome.text.toString().trim()
        val quantidadeStr = binding.etQuantidade.text.toString().trim()
        val descricao = binding.etDescricao.text.toString().trim()

        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(quantidadeStr)) {
            Toast.makeText(this, "Por favor, preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        val quantidade = quantidadeStr.toIntOrNull()
        if (quantidade == null || quantidade < 0) {
            Toast.makeText(this, "Quantidade inválida.", Toast.LENGTH_SHORT).show()
            return
        }

        val epi = Epi(
            id = epiId,
            nome = nome,
            quantidade = quantidade,
            descricao = descricao
        )

        val success = if (epiId == -1) {
            dbHelper.addEpi(epi)
        } else {
            dbHelper.updateEpi(epi)
        }

        if (success) {
            Toast.makeText(this, "EPI salvo com sucesso!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Erro ao salvar EPI.", Toast.LENGTH_SHORT).show()
        }
    }
}
