package com.diego.sistemafarmaciasb.controllers.auth;

import com.diego.sistemafarmaciasb.dtos.LoginDTO; // Você precisa criar este DTO
import com.diego.sistemafarmaciasb.dtos.funcionarios.UsuarioDTO;
import com.diego.sistemafarmaciasb.model.Funcionario;
import com.diego.sistemafarmaciasb.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public String login(@RequestBody LoginDTO loginDTO) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginDTO.login(), loginDTO.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        return tokenService.gerarToken((Funcionario) auth.getPrincipal());
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Funcionario funcionarioLogado = (Funcionario) authentication.getPrincipal();

        // Cria e retorna o novo DTO com id, nome e login
        UsuarioDTO perfilDTO = new UsuarioDTO(
                funcionarioLogado.getId(),
                funcionarioLogado.getNome(),
                funcionarioLogado.getLogin()
        );

        return ResponseEntity.ok(perfilDTO);
    }
}