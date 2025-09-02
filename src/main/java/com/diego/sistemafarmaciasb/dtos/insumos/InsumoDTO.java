package com.diego.sistemafarmaciasb.dtos.insumos;

import java.util.UUID;

public record InsumoDTO(
        UUID id,
        String nome,
        String descricaoDetalhada,
        String unidadeMedida) {
}