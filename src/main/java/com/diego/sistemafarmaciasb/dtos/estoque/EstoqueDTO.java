package com.diego.sistemafarmaciasb.dtos.estoque;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.UUID;

public record EstoqueDTO(
        UUID id,
        @NotBlank String numeroLote,
        @NotNull @Future LocalDate dataValidade,
        @NotNull @Positive int quantidade,
        @NotNull UUID itemId // Apenas um ID de item genérico
) {}