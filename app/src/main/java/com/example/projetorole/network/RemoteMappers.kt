package com.example.projetorole.network

import com.example.projetorole.data.model.Evento
import com.example.projetorole.data.model.Cupom

fun EventoNetwork.toModel(): Evento = Evento(
    id = id,
    nome = nome,
    local = local,
    horario = horario,
    checkIns = checkIns,
    pago = pago,
    preco = preco,
    descricao = descricao,
    estabelecimentoId = estabelecimentoId,
    estabelecimentoNome = estabelecimentoNome,
    latitude = latitude,
    longitude = longitude,
    cupomTitulo = cupomTitulo,
    cupomDescricao = cupomDescricao,
    cupomCheckinsNecessarios = cupomCheckinsNecessarios,
    paymentLink = paymentLink,
    imageUrl = imageUrl?.let { if (it.startsWith("/")) ApiClient.BASE_URL + it else it }
)

fun CupomUsuarioNetwork.toModel(): Cupom = Cupom(
    id = id,
    eventoId = eventoId,
    titulo = titulo,
    descricao = descricao,
    estabelecimentoNome = estabelecimentoNome,
    usado = usado,
    dataResgate = dataResgate
)