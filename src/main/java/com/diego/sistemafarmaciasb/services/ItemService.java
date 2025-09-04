package com.diego.sistemafarmaciasb.services;

import com.diego.sistemafarmaciasb.dtos.ItemDTO;
import com.diego.sistemafarmaciasb.model.Insumo;
import com.diego.sistemafarmaciasb.model.Item;
import com.diego.sistemafarmaciasb.model.Medicamento;
import com.diego.sistemafarmaciasb.repository.EstoqueRepository;
import com.diego.sistemafarmaciasb.repository.ItemRepository;
import org.hibernate.Hibernate; // Importe o Hibernate
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set; // Importe o Set
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final EstoqueRepository estoqueRepository;

    public ItemService(ItemRepository itemRepository, EstoqueRepository estoqueRepository) {
        this.itemRepository = itemRepository;
        this.estoqueRepository = estoqueRepository;
    }

    @Transactional(readOnly = true)
    public Page<ItemDTO> listarTodos(Pageable pageable, String dtype, String busca) {
        // 1. Cria a especificação para filtrar por tipo (Medicamento ou Insumo)
        // Usamos TYPE() para checar a classe da entidade na hierarquia de herança.
        Specification<Item> spec = (root, query, criteriaBuilder) -> {
            Class<?> typeClass = "MEDICAMENTO".equalsIgnoreCase(dtype) ? Medicamento.class : Insumo.class;
            return criteriaBuilder.equal(root.type(), typeClass);
        };

        // 2. Se um termo de busca foi fornecido, adiciona a condição de busca (WHERE ... AND ...)
        if (busca != null && !busca.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    // Procura o termo (em minúsculas) no nome OU na descrição
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + busca.toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("descricaoDetalhada")), "%" + busca.toLowerCase() + "%")
                    )
            );
        }

        // 3. Executa a busca com a especificação dinâmica e a paginação
        Page<Item> paginaDeEntidades = itemRepository.findAll(spec, pageable);

        Set<UUID> idsComEstoque = estoqueRepository.findDistinctItemIdsInEstoque();

        // Retorna o Page, que o Spring serializa para JSON (sem necessidade do PageDTO)
        return paginaDeEntidades.map(item -> paraDTO(item, idsComEstoque.contains(item.getId())));
    }


    @Transactional(readOnly = true)
    @Cacheable("itens-com-estoque")
    public List<ItemDTO> listarItensComEstoque() {
        List<Item> itensComEstoque = estoqueRepository.findItensComEstoqueDisponivel();

        return itensComEstoque.stream()
                .map(item -> paraDTO(item, true)) // Sintaxe da lambda corrigida (sem chaves desnecessárias)
                .collect(Collectors.toList());
    }

    private ItemDTO paraDTO(Item item, boolean possuiEstoque) {
        Object itemReal = Hibernate.unproxy(item);

        if (itemReal instanceof Medicamento medicamento) {
            // 2. A chamada ao construtor agora inclui TODOS os campos necessários
            return new ItemDTO(
                    medicamento.getId(),
                    medicamento.getNome(),
                    medicamento.getDescricaoDetalhada(),
                    medicamento.getUnidadeMedida(),
                    medicamento.isAtivo(),
                    "MEDICAMENTO",
                    medicamento.getTipo(),
                    possuiEstoque,
                    medicamento.getEstoqueMinimo()
            );
        } else if (itemReal instanceof Insumo insumo) {
            // 2. A chamada ao construtor agora inclui TODOS os campos necessários
            return new ItemDTO(
                    insumo.getId(),
                    insumo.getNome(),
                    insumo.getDescricaoDetalhada(),
                    insumo.getUnidadeMedida(),
                    insumo.isAtivo(),
                    "INSUMO",
                    null,
                    possuiEstoque,
                    insumo.getEstoqueMinimo()
            );
        }
        throw new IllegalArgumentException("Tipo de item desconhecido: " + item.getClass().getName());
    }
}