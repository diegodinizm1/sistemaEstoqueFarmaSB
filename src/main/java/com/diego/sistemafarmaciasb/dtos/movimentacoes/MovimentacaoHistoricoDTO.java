package com.diego.sistemafarmaciasb.dtos.movimentacoes;

import com.diego.sistemafarmaciasb.model.enums.TipoMovimentacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record MovimentacaoHistoricoDTO(
        UUID id,
        TipoMovimentacao tipoMovimentacao,
        int totalItens, // Quantos itens diferentes na movimentação
        int quantidadeTotal, // Soma de todas as quantidades
        String nomeSetor,
        String observacao,
        LocalDateTime dataMovimentacao,
        String nomeFuncionario
) {}
