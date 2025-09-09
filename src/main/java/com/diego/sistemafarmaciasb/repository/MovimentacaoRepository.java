package com.diego.sistemafarmaciasb.repository;

import com.diego.sistemafarmaciasb.dtos.dashboard.ConsumoSetorDTO;
import com.diego.sistemafarmaciasb.dtos.relatorios.RelatorioSaidaDiariaDTO;
import com.diego.sistemafarmaciasb.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, UUID> {

    // NENHUMA MUDANÇA NECESSÁRIA AQUI
    List<Movimentacao> findAllByOrderByDataMovimentacaoDesc();


    // ==================================================================================
    // CORREÇÃO APLICADA AQUI
    // Adicionado JOIN com 'm.itens mi' e depois com 'mi.item i' para acessar os nomes e quantidades.
    // ==================================================================================
    @Query("SELECT new com.diego.sistemafarmaciasb.dtos.relatorios.RelatorioSaidaDiariaDTO(" +
            "i.nome, m.setor.nome, SUM(mi.quantidade)) " +
            "FROM Movimentacao m " +
            "JOIN m.itens mi " +
            "JOIN mi.item i " +
            "WHERE m.tipoMovimentacao IN ('SAIDA', 'AJUSTE_SAIDA') " +
            "AND m.dataMovimentacao >= :inicioDoDia AND m.dataMovimentacao <= :fimDoDia " +
            "GROUP BY i.nome, m.setor.nome " +
            "ORDER BY i.nome, m.setor.nome")
    List<RelatorioSaidaDiariaDTO> findSaidasDiariasAgrupadas(LocalDateTime inicioDoDia, LocalDateTime fimDoDia);


    // ==================================================================================
    // CORREÇÃO APLICADA AQUI
    // Adicionado JOIN com 'm.itens mi' para somar as quantidades de 'mi.quantidade'.
    // ==================================================================================
    @Query("SELECT new com.diego.sistemafarmaciasb.dtos.dashboard.ConsumoSetorDTO(" +
            "m.setor.nome, SUM(mi.quantidade)) " +
            "FROM Movimentacao m " +
            "JOIN m.itens mi " +
            "WHERE m.setor IS NOT NULL " +
            "AND m.tipoMovimentacao IN ('SAIDA', 'AJUSTE_SAIDA') " +
            "AND m.dataMovimentacao BETWEEN :inicio AND :fim " +
            "GROUP BY m.setor.nome " +
            "ORDER BY SUM(mi.quantidade) DESC")
    List<ConsumoSetorDTO> findConsumoPorSetorNoPeriodo(LocalDateTime inicio, LocalDateTime fim);
}