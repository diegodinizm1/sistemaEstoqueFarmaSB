package com.diego.sistemafarmaciasb.dtos.dashboard;

import java.time.Month;

public record MovimentacaoMensalDTO(
        Integer mes,
        long entradas,
        long saidas
) {}
