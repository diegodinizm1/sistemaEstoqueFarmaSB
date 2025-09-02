package com.diego.sistemafarmaciasb.dtos.funcionarios;

import java.util.UUID;

public record FuncionarioDTO(
        UUID id,
        String nome,
        String login) {
}