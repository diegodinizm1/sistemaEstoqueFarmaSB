package com.diego.sistemafarmaciasb.dtos.movimentacoes;

public record ItemMovimentadoDTO(
        String nomeItem,
        String tipoItem,
        int quantidade
) {}
