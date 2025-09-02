package com.diego.sistemafarmaciasb.dtos;

import com.diego.sistemafarmaciasb.model.MedicamentoTipo;

import java.util.UUID;

public record ItemDTO(
        UUID id,
        String nome,
        String descricaoDetalhada,
        String unidadeMedida,
        boolean ativo,
        String dtype,
        MedicamentoTipo tipo
) {}
