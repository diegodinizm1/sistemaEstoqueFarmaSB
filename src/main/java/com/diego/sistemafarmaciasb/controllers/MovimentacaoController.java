package com.diego.sistemafarmaciasb.controllers;

import com.diego.sistemafarmaciasb.dtos.estoque.AjusteLoteDTO;
import com.diego.sistemafarmaciasb.dtos.movimentacoes.MovimentacaoDetalhesDTO;
import com.diego.sistemafarmaciasb.dtos.movimentacoes.MovimentacaoEntradaDTO;
import com.diego.sistemafarmaciasb.dtos.movimentacoes.MovimentacaoHistoricoDTO;
import com.diego.sistemafarmaciasb.dtos.movimentacoes.MovimentacaoSaidaDTO;
import com.diego.sistemafarmaciasb.services.MovimentacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/movimentacoes")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    public MovimentacaoController(MovimentacaoService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    @GetMapping
    public ResponseEntity<List<MovimentacaoHistoricoDTO>> listarHistorico() {
        return ResponseEntity.ok(movimentacaoService.listarHistorico());
    }

    @PostMapping("/entrada")
    public ResponseEntity<MovimentacaoHistoricoDTO> registrarEntrada(@Valid @RequestBody MovimentacaoEntradaDTO dto) {
        MovimentacaoHistoricoDTO novaMovimentacao = movimentacaoService.registrarEntrada(dto);
        return ResponseEntity.status(201).body(novaMovimentacao);
    }

    @PostMapping("/saida")
    public ResponseEntity<MovimentacaoHistoricoDTO> registrarSaida(@Valid @RequestBody MovimentacaoSaidaDTO dto) {
        MovimentacaoHistoricoDTO novaMovimentacao = movimentacaoService.registrarSaida(dto);
        return ResponseEntity.status(201).body(novaMovimentacao);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoDetalhesDTO> buscarDetalhesPorId(@PathVariable UUID id) {
        MovimentacaoDetalhesDTO detalhes = movimentacaoService.buscarDetalhesPorId(id);
        return ResponseEntity.ok(detalhes);
    }

}