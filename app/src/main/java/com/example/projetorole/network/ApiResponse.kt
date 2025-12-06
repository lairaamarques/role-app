@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.example.projetorole.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)

@Serializable
data class EventoNetwork(
    val id: Int,
    val nome: String,
    val local: String,
    val horario: String,
    val checkIns: Int,
    @SerialName("paid")
    val pago: Boolean = false,
    val preco: Double? = null,
    val descricao: String? = null,
    val estabelecimentoId: Int? = null,
    val estabelecimentoNome: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val cupomTitulo: String? = null,
    val cupomDescricao: String? = null,
    val cupomCheckinsNecessarios: Int = 1,
    val paymentLink: String? = null,
    val imageUrl: String? = null
)

@Serializable
data class CupomUsuarioNetwork(
    val id: Int,
    val eventoId: Int,
    val titulo: String,
    val descricao: String,
    val estabelecimentoNome: String? = null,
    val usado: Boolean,
    val dataResgate: String
)

@Serializable
data class CheckInDTO(
    val id: Int,
    val userId: Int,
    val eventoId: Int,
    val validatedAt: String?,
    val createdAt: String,
    val cupomGanho: CupomUsuarioNetwork? = null
)

@Serializable
data class CheckInRequest(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class EventoUpsertRequestNetwork(
    val nome: String,
    val local: String,
    val horario: String,
    val pago: Boolean,
    val preco: Double? = null,
    val descricao: String? = null,
    val latitude: Double,
    val longitude: Double,
    val cupomTitulo: String? = null,
    val cupomDescricao: String? = null,
    val cupomCheckinsNecessarios: Int = 1,
    val paymentLink: String? = null,
    val imageUrl: String? = null
)

@Serializable
data class UsuarioNetwork(
    val id: Int,
    val email: String,
    val nome: String? = null,
    val fotoUrl: String? = null
)

@Serializable
data class LoginRequest(
    val email: String,
    val senha: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val usuario: UsuarioNetwork
)

@Serializable
data class RegisterRequest(
    val nome: String? = null,
    val email: String,
    val senha: String
)

@Serializable
data class EstabelecimentoNetwork(
    val id: Int,
    val email: String,
    val nomeFantasia: String,
    val cnpj: String? = null,
    val fotoUrl: String? = null
)

@Serializable
data class EstabelecimentoAuthResponseNetwork(
    val token: String,
    val estabelecimento: EstabelecimentoNetwork
)

@Serializable
data class EstabelecimentoRegisterRequestNetwork(
    val email: String,
    val senha: String,
    val nomeFantasia: String,
    val cnpj: String? = null,
    val fotoUrl: String? = null
)

@Serializable
data class EstabelecimentoLoginRequestNetwork(
    val email: String,
    val senha: String
)