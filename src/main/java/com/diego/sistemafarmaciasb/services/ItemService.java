package com.diego.sistemafarmaciasb.services;


import com.diego.sistemafarmaciasb.dtos.ItemDTO;
import com.diego.sistemafarmaciasb.model.Insumo;
import com.diego.sistemafarmaciasb.model.Item;
import com.diego.sistemafarmaciasb.model.Medicamento;
import com.diego.sistemafarmaciasb.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true) // readOnly = true é uma otimização para consultas de leitura
    public List<ItemDTO> listarTodos() {
        List<Item> itens = itemRepository.findAll();

        return itens.stream()
                .map(this::paraDTO) // Converte cada Item para ItemDTO
                .collect(Collectors.toList());
    }

    private ItemDTO paraDTO(Item item) {
        if (item instanceof Medicamento medicamento) {
            // Se o item é uma instância de Medicamento...
            return new ItemDTO(
                    medicamento.getId(),
                    medicamento.getNome(),
                    medicamento.getDescricaoDetalhada(),
                    medicamento.getUnidadeMedida(),
                    medicamento.isAtivo(),
                    "MEDICAMENTO", // O valor do discriminador (dtype)
                    medicamento.getTipo() // O campo específico de Medicamento
            );
        } else if (item instanceof Insumo insumo) {
            // Se o item é uma instância de Insumo...
            return new ItemDTO(
                    insumo.getId(),
                    insumo.getNome(),
                    insumo.getDescricaoDetalhada(),
                    insumo.getUnidadeMedida(),
                    insumo.isAtivo(),
                    "INSUMO", // O valor do discriminador (dtype)
                    null // Insumo não tem o campo 'tipo', então ele é nulo
            );
        }
        // Fallback, embora não deva acontecer se só existirem Medicamentos e Insumos
        throw new IllegalArgumentException("Tipo de item desconhecido: " + item.getClass().getName());
    }
}
