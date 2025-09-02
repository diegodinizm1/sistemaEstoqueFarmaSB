package com.diego.sistemafarmaciasb.controllers;

import com.diego.sistemafarmaciasb.dtos.ItemDTO; // Um DTO base para itens
import com.diego.sistemafarmaciasb.services.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/itens")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<ItemDTO>> listarTodosItens() {
        List<ItemDTO> lista = itemService.listarTodos();
        return ResponseEntity.ok(lista);
    }
}