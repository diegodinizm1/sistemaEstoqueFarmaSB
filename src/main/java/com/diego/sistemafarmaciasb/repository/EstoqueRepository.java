package com.diego.sistemafarmaciasb.repository;

import com.diego.sistemafarmaciasb.dtos.dashboard.AlertaEstoqueDTO;
import com.diego.sistemafarmaciasb.dtos.dashboard.GraficoEstoqueDTO;
import com.diego.sistemafarmaciasb.dtos.dashboard.MovimentacaoMensalDTO;
import com.diego.sistemafarmaciasb.dtos.estoque.EstoqueSaldoDTO;
import com.diego.sistemafarmaciasb.model.Estoque;
import com.diego.sistemafarmaciasb.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface EstoqueRepository extends JpaRepository<Estoque, UUID> {

    List<Estoque> findByItemIdAndQuantidadeGreaterThanOrderByDataValidadeAsc(UUID itemId, int quantidade);

    @Query("SELECT new com.diego.sistemafarmaciasb.dtos.estoque.EstoqueSaldoDTO(" +
            "e.item.id, e.item.nome, " +
            "CASE WHEN TYPE(e.item) = Medicamento THEN 'MEDICAMENTO' ELSE 'INSUMO' END, " +
            "SUM(e.quantidade)) " +
            "FROM Estoque e " +
            "WHERE e.quantidade > 0 " +
            "AND (:busca IS NULL OR lower(e.item.nome) LIKE lower(concat('%', :busca, '%'))) " +
            "GROUP BY e.item.id, e.item.nome, TYPE(e.item)")
    Page<EstoqueSaldoDTO> findEstoqueSaldosComFiltro(Pageable pageable, @Param("busca") String busca);

    @Query("SELECT new com.diego.sistemafarmaciasb.dtos.estoque.EstoqueSaldoDTO(" +
            "e.item.id, e.item.nome, " +
            "CASE WHEN TYPE(e.item) = Medicamento THEN 'MEDICAMENTO' ELSE 'INSUMO' END, " +
            "SUM(e.quantidade)) " +
            "FROM Estoque e " +
            "WHERE e.quantidade > 0 " +
            "GROUP BY e.item.id, e.item.nome, TYPE(e.item)")
    Page<EstoqueSaldoDTO> findEstoqueSaldosSemFiltro(Pageable pageable);

    List<Estoque> findByItemIdOrderByDataValidadeAsc(UUID itemId);

    @Query("SELECT new com.diego.sistemafarmaciasb.dtos.dashboard.AlertaEstoqueDTO(" +
            "e.item.id, e.item.nome, CAST(SUM(e.quantidade) as string), 0) " +
            "FROM Estoque e " +
            "GROUP BY e.item.id, e.item.nome, e.item.estoqueMinimo " +
            "HAVING (e.item.estoqueMinimo > 0 AND SUM(e.quantidade) < e.item.estoqueMinimo) " +
            "OR (e.item.estoqueMinimo <= 0 AND SUM(e.quantidade) < :limiteEstoqueBaixoGeral)")
    List<AlertaEstoqueDTO> findItensComEstoqueBaixoComRegraHieraquica(@Param("limiteEstoqueBaixoGeral") long limiteEstoqueBaixoGeral);

    @Query("SELECT DISTINCT e.item FROM Estoque e WHERE e.quantidade > 0")
    List<Item> findItensComEstoqueDisponivel();

    @Query("""
        SELECT new com.diego.sistemafarmaciasb.dtos.dashboard.MovimentacaoMensalDTO(
            MONTH(m.dataMovimentacao),
            SUM(CASE WHEN m.tipoMovimentacao IN ('ENTRADA', 'AJUSTE_ENTRADA') THEN mi.quantidade ELSE 0 END),
            SUM(CASE WHEN m.tipoMovimentacao IN ('SAIDA') THEN mi.quantidade ELSE 0 END)
        )
        FROM Movimentacao m JOIN m.itens mi
        WHERE m.dataMovimentacao >= :dataLimite
        GROUP BY MONTH(m.dataMovimentacao)
        ORDER BY MONTH(m.dataMovimentacao)
    """)
    List<MovimentacaoMensalDTO> findMovimentacoesMensais(
            @Param("dataLimite") LocalDateTime dataLimite
    );

    List<Estoque> findByDataValidadeBetweenOrderByDataValidadeAsc(LocalDate hoje, LocalDate dataLimite);

    @Query("SELECT new com.diego.sistemafarmaciasb.dtos.dashboard.GraficoEstoqueDTO(" +
            "e.item.nome, SUM(e.quantidade)) " +
            "FROM Estoque e " +
            "GROUP BY e.item.nome " +
            "ORDER BY SUM(e.quantidade) DESC")
    List<GraficoEstoqueDTO> findTopEstoqueItens(Pageable pageable);

    @Query("SELECT DISTINCT e.item.id FROM Estoque e WHERE e.quantidade > 0")
    Set<UUID> findDistinctItemIdsInEstoque();
}