package com.diego.sistemafarmaciasb.dtos.movimentacoes;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record MovimentacaoSaidaDTO(
        @NotNull UUID itemId,
        @NotNull @Positive Integer quantidade,
        @NotNull UUID setorId,
        String observacao
) {}
