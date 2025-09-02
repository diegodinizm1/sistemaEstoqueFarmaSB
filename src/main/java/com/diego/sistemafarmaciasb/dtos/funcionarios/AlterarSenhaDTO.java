package com.diego.sistemafarmaciasb.dtos.funcionarios;

import jakarta.validation.constraints.NotBlank;

public record AlterarSenhaDTO(
        @NotBlank String senhaAtual,
        @NotBlank String novaSenha
) {}