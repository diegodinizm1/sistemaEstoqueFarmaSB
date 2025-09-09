package com.diego.sistemafarmaciasb.dtos.movimentacoes;

import com.diego.sistemafarmaciasb.model.enums.TipoMovimentacao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record MovimentacaoDetalhesDTO(
        UUID id,
        TipoMovimentacao tipoMovimentacao,
        LocalDateTime dataMovimentacao,
        String observacao,
        String nomeFuncionario,
        String nomeSetor, // Será nulo se não for uma saída
        List<ItemMovimentadoDTO> itens // A lista de itens
) {}
