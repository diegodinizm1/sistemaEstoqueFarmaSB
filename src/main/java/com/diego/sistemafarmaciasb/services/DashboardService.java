package com.diego.sistemafarmaciasb.services;

import com.diego.sistemafarmaciasb.dtos.dashboard.*;
import com.diego.sistemafarmaciasb.model.exceptions.ValidacaoException;
import com.diego.sistemafarmaciasb.repository.EstoqueRepository;
import com.diego.sistemafarmaciasb.repository.ItemRepository;
import com.diego.sistemafarmaciasb.repository.MovimentacaoRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true) // A maioria dos métodos são de leitura
public class DashboardService {

    private final ItemRepository itemRepository;
    private final EstoqueRepository estoqueRepository;
    private final ConfiguracaoService configuracaoService;
    private final MovimentacaoRepository movimentacaoRepository;

    public DashboardService(ItemRepository itemRepository, EstoqueRepository estoqueRepository, ConfiguracaoService configuracaoService, MovimentacaoRepository movimentacaoRepository) {
        this.itemRepository = itemRepository;
        this.estoqueRepository = estoqueRepository;
        this.configuracaoService = configuracaoService;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    /**
     * Calcula e retorna as estatísticas principais para os cards do dashboard.
     */
    @Cacheable("dashboard-geral")
    public DashboardStatsDTO getStats() {
        int diasAlertaVencimento = Integer.parseInt(
                configuracaoService.getValor("DIAS_ALERTA_VENCIMENTO", "30")
        );
        long limiteEstoqueBaixo = Long.parseLong(
                configuracaoService.getValor("LIMITE_ESTOQUE_BAIXO", "10") // Padrão de 10 se não existir
        );
        long totalMedicamentos = itemRepository.countMedicamentos();
        long totalInsumos = itemRepository.countInsumos();

        LocalDate hoje = LocalDate.now();
        LocalDate dataLimite = hoje.plusDays(diasAlertaVencimento);

        long lotesProximosVencimento = estoqueRepository.findByDataValidadeBetweenOrderByDataValidadeAsc(hoje, dataLimite).size();
        long itensEstoqueBaixo = estoqueRepository.findItensComEstoqueBaixoComRegraHieraquica(limiteEstoqueBaixo).size();

        long medicamentosComEstoque = itemRepository.countMedicamentosComEstoque();
        long insumosComEstoque = itemRepository.countInsumosComEstoque();

        return new DashboardStatsDTO(totalMedicamentos, totalInsumos, lotesProximosVencimento, itensEstoqueBaixo, medicamentosComEstoque, insumosComEstoque);
    }

    /**
     * Retorna a lista de lotes que estão próximos de vencer.
     */
    @Cacheable("dashboard-lotes-vencimento")
    public List<AlertaEstoqueDTO> getLotesProximosVencimento() {
        int diasAlertaVencimento = Integer.parseInt(
                configuracaoService.getValor("DIAS_ALERTA_VENCIMENTO", "30")
        );
        LocalDate hoje = LocalDate.now();
        LocalDate dataLimite = hoje.plusDays(diasAlertaVencimento);

        return estoqueRepository.findByDataValidadeBetweenOrderByDataValidadeAsc(hoje, dataLimite).stream()
                .map(lote -> {
                    long diasParaVencer = ChronoUnit.DAYS.between(hoje, lote.getDataValidade());
                    String extraInfo = "Lote: " + lote.getNumeroLote() + " (Qtd: " + lote.getQuantidade() + ")";
                    return new AlertaEstoqueDTO(lote.getItem().getId(), lote.getItem().getNome(), extraInfo, (int) diasParaVencer);
                })
                .collect(Collectors.toList());
    }

    /**
     * Retorna a lista de itens cujo estoque total está abaixo do mínimo definido.
     */
    @Cacheable("dashboard-estoque-baixo")
    public List<AlertaEstoqueDTO> getItensComEstoqueBaixo() {
        long limiteEstoqueBaixo = Long.parseLong(
                configuracaoService.getValor("LIMITE_ESTOQUE_BAIXO", "10")
        );
        return estoqueRepository.findItensComEstoqueBaixoComRegraHieraquica(limiteEstoqueBaixo);
    }

    @Cacheable("dashboard-movimentacoes-mes")
    public List<MovimentacaoMensalDTO> getMovimentacoesPorMes() {
        LocalDateTime dataLimite = LocalDateTime.now().minusYears(1);
        return estoqueRepository.findMovimentacoesMensais(dataLimite);
    }

    @Cacheable("dashboard-top-itens-estoque")
    public List<GraficoEstoqueDTO> getDadosGraficoEstoque() {
        // Pede ao repositório os 5 itens com maior quantidade em estoque
        Pageable topFive = PageRequest.of(0, 5);
        return estoqueRepository.findTopEstoqueItens(topFive);
    }

    @Cacheable(value = "dashboard-consumo-setor", key = "#periodo")
    public List<ConsumoSetorDTO> getConsumoPorSetor(String periodo) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime inicio;
        LocalDateTime fim = agora;

        switch (periodo.toUpperCase()) {
            case "DIA":
                inicio = agora.toLocalDate().atStartOfDay();
                break;
            case "MES":
                inicio = agora.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay();
                break;
            case "ANO":
                inicio = agora.with(TemporalAdjusters.firstDayOfYear()).toLocalDate().atStartOfDay();
                break;
            default:
                throw new ValidacaoException("Período inválido. Use 'DIA', 'MES' ou 'ANO'.");
        }

        return movimentacaoRepository.findConsumoPorSetorNoPeriodo(inicio, fim);
    }
}
