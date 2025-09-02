package com.diego.sistemafarmaciasb.dtos.dashboard;

public record DashboardStatsDTO(
        long totalMedicamentos,
        long totalInsumos,
        long lotesProximosVencimento,
        long itensEstoqueBaixo,
        long medicamentosComEstoque,
        long insumosComEstoque
) {}