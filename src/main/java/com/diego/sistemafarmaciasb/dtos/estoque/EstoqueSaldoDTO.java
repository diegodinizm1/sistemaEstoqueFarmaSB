package com.diego.sistemafarmaciasb.dtos.estoque;

import java.util.UUID;

public record EstoqueSaldoDTO(
        UUID itemId,
        String nomeItem,
        String dtype, // "MEDICAMENTO" ou "INSUMO"
        long quantidadeTotal // A soma de todos os lotes
) {}
