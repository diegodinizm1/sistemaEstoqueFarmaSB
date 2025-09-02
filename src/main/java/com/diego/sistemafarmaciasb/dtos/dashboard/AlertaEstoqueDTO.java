package com.diego.sistemafarmaciasb.dtos.dashboard;

import java.util.UUID;

public record AlertaEstoqueDTO(
        UUID itemId,
        String nomeItem,
        String extraInfo,
        int diasParaVencer
) {}