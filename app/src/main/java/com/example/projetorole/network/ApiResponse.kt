package com.example.projetorole.network

import kotlinx.serialization.Serializable

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
    val pago: Boolean,
    val preco: Double?
)

@Serializable
data class CupomNetwork(
    val id: Int,
    val titulo: String,
    val descricao: String,
    val local: String,
    val disponivel: Boolean
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