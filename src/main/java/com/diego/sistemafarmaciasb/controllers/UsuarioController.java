package com.diego.sistemafarmaciasb.controllers;

import com.diego.sistemafarmaciasb.dtos.funcionarios.AlterarSenhaDTO;
import com.diego.sistemafarmaciasb.dtos.funcionarios.PerfilUpdateDTO;
import com.diego.sistemafarmaciasb.dtos.funcionarios.UsuarioDTO;
import com.diego.sistemafarmaciasb.services.FuncionarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final FuncionarioService funcionarioService;

    public UsuarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    // Endpoint para o usuário buscar seus próprios dados (já temos o /api/auth/me)

    @PutMapping("/perfil")
    public ResponseEntity<UsuarioDTO> atualizarPerfil(@Valid @RequestBody PerfilUpdateDTO dto) {
        return ResponseEntity.ok(funcionarioService.atualizarPerfil(dto));
    }

    @PutMapping("/perfil/alterar-senha")
    public ResponseEntity<Void> alterarSenha(@Valid @RequestBody AlterarSenhaDTO dto) {
        funcionarioService.alterarSenha(dto);
        return ResponseEntity.noContent().build();
    }
}