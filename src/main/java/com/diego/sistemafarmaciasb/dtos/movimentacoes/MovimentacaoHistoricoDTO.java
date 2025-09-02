package com.diego.sistemafarmaciasb.dtos.movimentacoes;

import com.diego.sistemafarmaciasb.model.enums.TipoMovimentacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record MovimentacaoHistoricoDTO(
        UUID id,
        TipoMovimentacao tipoMovimentacao,
        String nomeItem,
        Integer quantidade,
        String nomeSetor,
        String observacao,
        LocalDateTime dataMovimentacao,
        String tipoItem
) {}
