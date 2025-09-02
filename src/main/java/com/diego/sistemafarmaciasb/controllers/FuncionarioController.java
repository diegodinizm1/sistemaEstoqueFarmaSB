package com.diego.sistemafarmaciasb.controllers;

import com.diego.sistemafarmaciasb.dtos.funcionarios.FuncionarioCreateDTO;
import com.diego.sistemafarmaciasb.dtos.funcionarios.FuncionarioDTO;
import com.diego.sistemafarmaciasb.dtos.funcionarios.FuncionarioListaDTO;
import com.diego.sistemafarmaciasb.dtos.funcionarios.FuncionarioUpdateDTO;
import com.diego.sistemafarmaciasb.services.FuncionarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    @Autowired
    private FuncionarioService funcionarioService;

    @GetMapping
    public ResponseEntity<List<FuncionarioListaDTO>> listarTodos() {
        return ResponseEntity.ok(funcionarioService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<FuncionarioListaDTO> criar(@Valid @RequestBody FuncionarioCreateDTO dto) {
        return ResponseEntity.status(201).body(funcionarioService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioListaDTO> atualizar(@PathVariable UUID id, @Valid @RequestBody FuncionarioUpdateDTO dto) {
        return ResponseEntity.ok(funcionarioService.atualizar(id, dto));
    }
}