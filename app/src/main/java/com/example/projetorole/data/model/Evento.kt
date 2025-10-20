package com.example.projetorole.data.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class Evento(
    val id: Int,
    val nome: String,
    val horario: String,
    val local: String,
    val checkIns: Int,
    val nota: Double? = null,
    val pago: Boolean = true,
    val preco: Double? = null,
    val descricao: String? = null,
    val estabelecimentoId: Int? = null,
    val estabelecimentoNome: String? = null
) {
    fun getPrecoFormatado(): String {
        return if (pago && preco != null) {
            "R$ ${String.format("%.0f", preco)}"
        } else {
            "Gratuito"
        }
    }

    fun getDataFormatada(): String {
        val date = parseHorario() ?: return horario
        val calendar = Calendar.getInstance().apply { time = date }
        val diaSemana = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale("pt", "BR"))
            ?.replaceFirstChar { it.uppercase(Locale("pt", "BR")) } ?: ""
        val dia = calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
        val mes = (calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val ano = calendar.get(Calendar.YEAR)
        return "$diaSemana, $dia/$mes/$ano"
    }

    fun getDataExtenso(): String {
        val date = parseHorario() ?: return horario
        val formatter = SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
        val texto = formatter.format(date)
        return texto.replaceFirstChar { it.uppercase(Locale("pt", "BR")) }
    }

    fun getHoraFormatada(): String {
        val date = parseHorario() ?: return horario
        val formatter = SimpleDateFormat("HH'h'", Locale("pt", "BR"))
        return formatter.format(date)
    }

    private fun parseHorario(): Date? = try {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault()).parse(horario)
    } catch (e: Exception) {
        null
    }
}