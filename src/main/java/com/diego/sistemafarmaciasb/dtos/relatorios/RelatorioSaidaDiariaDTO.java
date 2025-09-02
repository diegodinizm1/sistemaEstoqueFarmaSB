// Em: dtos.relatorios
package com.diego.sistemafarmaciasb.dtos.relatorios;

// DTO para carregar os dados agregados do relatório
public record RelatorioSaidaDiariaDTO(
        String nomeItem,
        String nomeSetor,
        Long quantidadeTotal
) {}