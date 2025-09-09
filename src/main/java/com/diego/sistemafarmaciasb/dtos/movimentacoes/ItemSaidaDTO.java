package com.diego.sistemafarmaciasb.dtos.movimentacoes;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

// DTO para representar cada item em uma requisição de saída
public record ItemSaidaDTO(
        @NotNull UUID itemId,
        @NotNull @Positive Integer quantidade
) {}