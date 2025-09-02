package com.diego.sistemafarmaciasb.dtos.estoque;

import java.time.LocalDate;
import java.util.UUID;

public record EstoqueListaDTO(
        UUID id,
        String numeroLote,
        LocalDate dataValidade,
        int quantidade,
        UUID itemId,
        String nomeItem,
        String tipoItem // "MEDICAMENTO" ou "INSUMO"
) {}
