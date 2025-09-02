package com.diego.sistemafarmaciasb.controllers;

import com.diego.sistemafarmaciasb.dtos.medicamentos.MedicamentoDTO;
import com.diego.sistemafarmaciasb.services.MedicamentoService;
import jakarta.validation.Valid; // Importe o @Valid
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/medicamentos")
public class MedicamentoController {

    // 1. Injeção de dependência via construtor
    private final MedicamentoService medicamentoService;

    public MedicamentoController(MedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    @GetMapping
    public ResponseEntity<List<MedicamentoDTO>> listarTodos() {
        List<MedicamentoDTO> lista = medicamentoService.listarTodos();
        // Retorna 200 OK com a lista, ou 204 No Content se a lista estiver vazia (opcional, mas boa prática)
        return lista.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<MedicamentoDTO>> buscarPorId(@PathVariable UUID id) {
        Optional<MedicamentoDTO> dto = medicamentoService.buscarPorId(id); // Assumindo que o serviço lança exceção se não encontrar
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<MedicamentoDTO> criar(@Valid @RequestBody MedicamentoDTO medicamentoDTO) {
        MedicamentoDTO novoMedicamento = medicamentoService.salvar(medicamentoDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoMedicamento.id())
                .toUri();

        return ResponseEntity.created(location).body(novoMedicamento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicamentoDTO> atualizar(@PathVariable UUID id, @Valid @RequestBody MedicamentoDTO medicamentoDTO) {
        // 2. Retornando o objeto atualizado no corpo da resposta
        MedicamentoDTO medicamentoAtualizado = medicamentoService.atualizar(id, medicamentoDTO);
        return ResponseEntity.ok(medicamentoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        medicamentoService.removerPorId(id);
        return ResponseEntity.noContent().build();
    }
}