package com.diego.sistemafarmaciasb.controllers;

import com.diego.sistemafarmaciasb.services.ConfiguracaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/configuracoes")
public class ConfiguracaoController {

    private final ConfiguracaoService configuracaoService;

    public ConfiguracaoController(ConfiguracaoService configuracaoService) {
        this.configuracaoService = configuracaoService;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getTodasConfiguracoes() {
        return ResponseEntity.ok(configuracaoService.getTodasConfiguracoes());
    }

    @PutMapping
    public ResponseEntity<Void> salvarConfiguracoes(@RequestBody Map<String, String> configuracoes) {
        configuracaoService.salvarConfiguracoes(configuracoes);
        return ResponseEntity.ok().build();
    }
}