package com.diego.sistemafarmaciasb.dtos.movimentacoes;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

public record MovimentacaoSaidaDTO(
        @NotNull UUID setorId,
        String observacao,
        @NotEmpty @Valid List<ItemSaidaDTO> itens // Lista de itens
) {}