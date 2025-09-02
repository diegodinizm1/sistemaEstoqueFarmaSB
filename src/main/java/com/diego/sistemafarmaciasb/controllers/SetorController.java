package com.diego.sistemafarmaciasb.controllers;

import com.diego.sistemafarmaciasb.dtos.setor.SetorDTO;
import com.diego.sistemafarmaciasb.services.SetorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/setores")
public class SetorController {

    @Autowired
    private SetorService setorService;

    @GetMapping
    public List<SetorDTO> listarTodos() {
        return setorService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SetorDTO> buscarPorId(@PathVariable UUID id) {
        return setorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public SetorDTO criar(@RequestBody SetorDTO setorDTO) {
        return setorService.salvar(setorDTO);
    }
}