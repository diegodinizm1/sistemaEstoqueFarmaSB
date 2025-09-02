package com.diego.sistemafarmaciasb.dtos.funcionarios;

import jakarta.validation.constraints.NotBlank;

public record PerfilUpdateDTO(@NotBlank String nome, @NotBlank String login) {}


