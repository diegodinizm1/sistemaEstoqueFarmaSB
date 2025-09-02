package com.diego.sistemafarmaciasb.dtos.medicamentos;

import com.diego.sistemafarmaciasb.model.MedicamentoTipo;
import java.util.UUID;

public record MedicamentoDTO(
        UUID id,
        String nome,
        String descricaoDetalhada,
        String unidadeMedida,
        MedicamentoTipo tipo,
        int estoqueMinimo
)
{
}