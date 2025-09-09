package com.diego.sistemafarmaciasb.controllers;

import com.diego.sistemafarmaciasb.dtos.ItemDTO; // Um DTO base para itens
import com.diego.sistemafarmaciasb.repository.ItemRepository;
import com.diego.sistemafarmaciasb.services.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/itens")
public class ItemController {

    private final ItemService itemService;
    private final ItemRepository itemRepository;

    public ItemController(ItemService itemService, ItemRepository itemRepository) {
        this.itemService = itemService;
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public ResponseEntity<Page<ItemDTO>> listarTodosItens(
            Pageable pageable,
            @RequestParam(required = false) String dtype,
            @RequestParam(required = false) String busca) {

        Page<ItemDTO> paginaItens = itemService.listarTodos(pageable, dtype, busca);
        return ResponseEntity.ok(paginaItens);
    }

    @GetMapping("/add-modal")
    public ResponseEntity<List<ItemDTO>> listarTodosItensAdd(){
        return ResponseEntity.ok(itemService.listarTodosParaAdd());
    }

    @GetMapping("/com-estoque")
    public ResponseEntity<List<ItemDTO>> listarComEstoqueDisponivel() {
        List<ItemDTO> itensComEstoque = itemService.listarItensComEstoque();
        return ResponseEntity.ok(itensComEstoque);
    }
}