package com.diego.sistemafarmaciasb.dtos.dashboard;

public record GraficoEstoqueDTO(
        String nomeItem,
        long quantidadeTotal
) {}