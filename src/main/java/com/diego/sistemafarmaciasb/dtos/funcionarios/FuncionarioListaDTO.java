package com.diego.sistemafarmaciasb.dtos.funcionarios;

import java.util.UUID;

public record FuncionarioListaDTO(UUID id, String nome, String login, boolean ativo) {}
