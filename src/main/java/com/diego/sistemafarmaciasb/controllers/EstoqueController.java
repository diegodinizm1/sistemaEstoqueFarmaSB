package com.diego.sistemafarmaciasb.controllers;

import com.diego.sistemafarmaciasb.dtos.estoque.AjusteLoteDTO;
import com.diego.sistemafarmaciasb.dtos.estoque.EstoqueListaDTO;
import com.diego.sistemafarmaciasb.dtos.estoque.EstoqueSaldoDTO;
import com.diego.sistemafarmaciasb.services.EstoqueService;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<EstoqueSaldoDTO>> listarSaldos() {
        List<EstoqueSaldoDTO> saldos = estoqueService.listarSaldosDeEstoque();
        return ResponseEntity.ok(saldos);
    }

    /**
     * Endpoint de Detalhe: Retorna todos os lotes de um item específico.
     * Usado pelo modal "Ver Lotes" no front-end.
     * Rota: GET /api/estoque/item/{itemId}
     */
    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<EstoqueListaDTO>> listarLotesPorItem(@PathVariable UUID itemId) {
        List<EstoqueListaDTO> lotes = estoqueService.listarLotesPorItem(itemId);
        return ResponseEntity.ok(lotes);
    }

    /**
     * Endpoint de Ajuste: Atualiza a quantidade ou validade de um lote específico.
     * Usado pelo modal de "Ajustar Lote" no front-end.
     * Rota: PUT /api/estoque/ajustar/{estoqueId}
     */
    @PutMapping("/ajustar/{estoqueId}")
    public ResponseEntity<EstoqueListaDTO> ajustarLote(
            @PathVariable UUID estoqueId,
            @Valid @RequestBody AjusteLoteDTO dto) {
        EstoqueListaDTO loteAtualizado = estoqueService.ajustarLote(estoqueId, dto);
        return ResponseEntity.ok(loteAtualizado);
    }

    // ----------------------------------------------------------------------------------
    // OBSERVAÇÃO: O endpoint POST para criar um lote foi REMOVIDO deste controller.
    // A criação de um novo lote agora é uma responsabilidade do MovimentacaoController,
    // através do endpoint POST /api/movimentacoes/entrada.
    // ----------------------------------------------------------------------------------
}