package com.diego.sistemafarmaciasb.dtos.funcionarios;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FuncionarioUpdateDTO(@NotBlank String nome, @NotBlank String login, String senha, @NotNull Boolean ativo) {}
