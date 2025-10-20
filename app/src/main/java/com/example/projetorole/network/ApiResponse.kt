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
    val preco: Double? = null,
    val descricao: String? = null,
    val estabelecimentoId: Int? = null,
    val estabelecimentoNome: String? = null
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

@Serializable
data class EventoUpsertRequestNetwork(
    val nome: String,
    val local: String,
    val horario: String,
    val pago: Boolean,
    val preco: Double? = null,
    val descricao: String? = null
)