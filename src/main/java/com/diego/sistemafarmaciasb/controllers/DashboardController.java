package com.diego.sistemafarmaciasb.controllers;

import com.diego.sistemafarmaciasb.dtos.dashboard.*;
import com.diego.sistemafarmaciasb.services.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    @GetMapping("/vencimento")
    public ResponseEntity<List<AlertaEstoqueDTO>> getLotesProximosVencimento() {
        return ResponseEntity.ok(dashboardService.getLotesProximosVencimento());
    }

    @GetMapping("/estoque-baixo")
    public ResponseEntity<List<AlertaEstoqueDTO>> getItensComEstoqueBaixo() {
        return ResponseEntity.ok(dashboardService.getItensComEstoqueBaixo());
    }

    @GetMapping("/movimentacoes-por-mes")
    public ResponseEntity<List<MovimentacaoMensalDTO>> getMovimentacoesPorMes() {
        List<MovimentacaoMensalDTO> movimentacoes = dashboardService.getMovimentacoesPorMes();
        return ResponseEntity.ok(movimentacoes);

    }

    @GetMapping("/grafico-estoque")
    public ResponseEntity<List<GraficoEstoqueDTO>> getDadosGrafico() {
        return ResponseEntity.ok(dashboardService.getDadosGraficoEstoque());
    }

    @GetMapping("/consumo-setor")
    public ResponseEntity<List<ConsumoSetorDTO>> getConsumoPorSetor(@RequestParam(defaultValue = "MES") String periodo) {
        return ResponseEntity.ok(dashboardService.getConsumoPorSetor(periodo));
    }
}