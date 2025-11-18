package com.example.projetorole.backend.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)

@Serializable
data class EventoRequest(
    val nome: String,
    val local: String,
    val horario: String,
    val pago: Boolean,
    val preco: Double? = null,
    val descricao: String? = null,
    val estabelecimentoId: Int? = null,
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class EventoResponse(
    val id: Int,
    val nome: String,
    val local: String,
    val horario: String,
    val checkIns: Int,
    val pago: Boolean,
    val preco: Double? = null,
    val descricao: String? = null,
    val estabelecimentoNome: String? = null,
    val estabelecimentoId: Int?
)

@Serializable
data class EventoNetwork(
    val id: Int,
    val nome: String,
    val local: String,
    val horario: String,
    val checkIns: Int,
    val pago: Boolean,
    val preco: Double? = null,
    val descricao: String? = null,
    val estabelecimentoId: Int? = null,
    val estabelecimentoNome: String? = null
)

@Serializable
data class CupomRequest(
    val titulo: String,
    val descricao: String,
    val local: String,
    val disponivel: Boolean
)

@Serializable
data class CupomResponse(
    val id: Int,
    val titulo: String,
    val descricao: String,
    val local: String,
    val disponivel: Boolean,
    val estabelecimentoId: Int,
    val estabelecimentoNome: String?
)

@Serializable
data class UsuarioDTO(
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
    val usuario: UsuarioDTO
)

@Serializable
data class RegisterRequest(
    val nome: String? = null,
    val email: String,
    val senha: String
)

@Serializable
data class EstabelecimentoDTO(
    val id: Int,
    val email: String,
    val nomeFantasia: String,
    val cnpj: String? = null,
    val fotoUrl: String? = null
)

@Serializable
data class EstabelecimentoRegisterRequest(
    val email: String,
    val senha: String,
    val nomeFantasia: String,
    val cnpj: String? = null,
    val fotoUrl: String? = null
)

@Serializable
data class EstabelecimentoLoginRequest(
    val email: String,
    val senha: String
)

@Serializable
data class EstabelecimentoAuthResponse(
    val token: String,
    val estabelecimento: EstabelecimentoDTO
)

@Serializable
data class CheckInRequest(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class CheckInDTO(
    val id: Int,
    val userId: Int,
    val eventoId: Int,
    val validatedAt: String?,
    val createdAt: String
)
