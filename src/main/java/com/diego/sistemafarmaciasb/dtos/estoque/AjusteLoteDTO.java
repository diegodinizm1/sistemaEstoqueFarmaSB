package com.diego.sistemafarmaciasb.dtos.estoque;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

/**
 * DTO utilizado para receber os dados de um ajuste em um lote de estoque existente.
 */
public record AjusteLoteDTO(

        @NotNull(message = "A nova quantidade é obrigatória.")
        @PositiveOrZero(message = "A quantidade não pode ser negativa.")
        Integer novaQuantidade,

        @NotNull(message = "A nova data de validade é obrigatória.")
        @FutureOrPresent(message = "A data de validade não pode ser no passado.")
        LocalDate novaDataValidade,

        @NotBlank(message = "A observação é obrigatória para um ajuste.")
        String observacao
) {}