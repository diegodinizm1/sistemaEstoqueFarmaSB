package com.diego.sistemafarmaciasb.dtos.movimentacoes;

import com.diego.sistemafarmaciasb.model.enums.TipoMovimentacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record MovimentacaoDetalhesDTO(
        UUID id,
        TipoMovimentacao tipoMovimentacao,
        LocalDateTime dataMovimentacao,
        String observacao,

        // Detalhes do Item
        String nomeItem,
        String tipoItem, // "MEDICAMENTO" ou "INSUMO"

        // Detalhes do Usuário
        String nomeFuncionario,

        // Detalhes da Quantidade e Destino
        Integer quantidade,
        String nomeSetor // Será nulo se não for uma saída
) {}
