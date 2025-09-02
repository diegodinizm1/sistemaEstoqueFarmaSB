package com.diego.sistemafarmaciasb.dtos.movimentacoes;

import com.diego.sistemafarmaciasb.model.enums.TipoMovimentacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record MovimentacaoDTO(
        @NotNull TipoMovimentacao tipoMovimentacao,
        @NotNull UUID itemId,
        @NotNull @Positive Integer quantidade,
        UUID setorId,
        String observacao
) {}
