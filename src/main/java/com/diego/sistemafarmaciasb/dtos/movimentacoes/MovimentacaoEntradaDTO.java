package com.diego.sistemafarmaciasb.dtos.movimentacoes;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record MovimentacaoEntradaDTO(
        String observacao,
        @NotEmpty @Valid List<ItemEntradaDTO> itens// Garante que a data seja no futuro
) {}
