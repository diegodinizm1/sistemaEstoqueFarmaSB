package com.diego.sistemafarmaciasb.dtos.movimentacoes;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.UUID;

// DTO para representar cada item em uma requisição de entrada
public record ItemEntradaDTO(
        @NotNull UUID itemId,
        @NotNull @Positive Integer quantidade,
        @NotBlank String numeroLote,
        @NotNull @Future LocalDate dataValidade
) {}