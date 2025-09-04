package com.diego.sistemafarmaciasb.controllers;

import com.diego.sistemafarmaciasb.dtos.estoque.AjusteLoteDTO;
import com.diego.sistemafarmaciasb.dtos.estoque.EstoqueListaDTO;
import com.diego.sistemafarmaciasb.dtos.estoque.EstoqueSaldoDTO;
import com.diego.sistemafarmaciasb.services.EstoqueService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    /**
     * Endpoint principal (Mestre): Retorna a lista de saldos totais de cada item em estoque.
     * Usado para popular a tabela principal da página de Estoque.
     * Rota: GET /api/estoque
     */
    @GetMapping
    public ResponseEntity<Page<EstoqueSaldoDTO>> listarSaldos(
            Pageable pageable,
            @RequestParam(required = false) String busca) {
        return ResponseEntity.ok(estoqueService.listarSaldosDeEstoque(pageable, busca));
    }


    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<EstoqueListaDTO>> listarLotesPorItem(@PathVariable UUID itemId) {
        List<EstoqueListaDTO> lotes = estoqueService.listarLotesPorItem(itemId);
        return ResponseEntity.ok(lotes);
    }

    @PutMapping("/ajustar/{estoqueId}")
    public ResponseEntity<EstoqueListaDTO> ajustarLote(
            @PathVariable UUID estoqueId,
            @Valid @RequestBody AjusteLoteDTO dto) {
        EstoqueListaDTO loteAtualizado = estoqueService.ajustarLote(estoqueId, dto);
        return ResponseEntity.ok(loteAtualizado);
    }

}