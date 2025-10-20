package com.example.projetorole.network

import com.example.projetorole.data.model.Cupom
import com.example.projetorole.data.model.Evento

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
    estabelecimentoNome = estabelecimentoNome
)

fun CupomNetwork.toModel(): Cupom = Cupom(
    id = id,
    titulo = "🎫 $titulo",
    descricao = descricao,
    local = local,
    disponivel = disponivel
)