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
    // Busca todas as movimentações ordenando pela data mais recente
    List<Movimentacao> findAllByOrderByDataMovimentacaoDesc();

    @Query("SELECT new com.diego.sistemafarmaciasb.dtos.relatorios.RelatorioSaidaDiariaDTO(" +
            "m.item.nome, m.setor.nome, SUM(m.quantidade)) " +
            "FROM Movimentacao m " +
            "WHERE m.tipoMovimentacao IN ('SAIDA', 'AJUSTE_SAIDA') " +
            "AND m.dataMovimentacao >= :inicioDoDia AND m.dataMovimentacao <= :fimDoDia " +
            "GROUP BY m.item.nome, m.setor.nome " +
            "ORDER BY m.item.nome, m.setor.nome")
    List<RelatorioSaidaDiariaDTO> findSaidasDiariasAgrupadas(LocalDateTime inicioDoDia, LocalDateTime fimDoDia);

    @Query("SELECT new com.diego.sistemafarmaciasb.dtos.dashboard.ConsumoSetorDTO(" +
            "m.setor.nome, SUM(m.quantidade)) " +
            "FROM Movimentacao m " +
            "WHERE m.setor IS NOT NULL " +
            "AND m.tipoMovimentacao IN ('SAIDA', 'AJUSTE_SAIDA') " +
            "AND m.dataMovimentacao BETWEEN :inicio AND :fim " +
            "GROUP BY m.setor.nome " +
            "ORDER BY SUM(m.quantidade) DESC")
    List<ConsumoSetorDTO> findConsumoPorSetorNoPeriodo(LocalDateTime inicio, LocalDateTime fim);
}
