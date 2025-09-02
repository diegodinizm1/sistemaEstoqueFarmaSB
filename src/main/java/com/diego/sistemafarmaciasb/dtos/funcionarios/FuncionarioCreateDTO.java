package com.diego.sistemafarmaciasb.dtos.funcionarios;

import jakarta.validation.constraints.NotBlank;

public record FuncionarioCreateDTO(@NotBlank String nome, @NotBlank String login, @NotBlank String senha, @NotBlank(message = "A senha de confirmação do administrador é obrigatória")
String senhaAdminConfirmacao) {}
