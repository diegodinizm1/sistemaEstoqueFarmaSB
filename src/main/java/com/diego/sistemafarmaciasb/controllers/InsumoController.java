package com.diego.sistemafarmaciasb.controllers;

import com.diego.sistemafarmaciasb.dtos.insumos.InsumoDTO;
import com.diego.sistemafarmaciasb.services.InsumoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/insumos")
public class InsumoController {

    @Autowired
    private InsumoService insumoService;

    @GetMapping
    public List<InsumoDTO> listarTodos() {
        return insumoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsumoDTO> buscarPorId(@PathVariable UUID id) {
        return insumoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public InsumoDTO criar(@RequestBody InsumoDTO insumoDTO) {
        return insumoService.salvar(insumoDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsumoDTO> atualizar(@PathVariable UUID id, @Valid @RequestBody InsumoDTO insumoDTO) {
        InsumoDTO insumoAtualizado = insumoService.atualizar(id, insumoDTO);
        return ResponseEntity.ok(insumoAtualizado);
    }

    // --- ENDPOINT DE EXCLUSÃO (DELETE) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        insumoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}