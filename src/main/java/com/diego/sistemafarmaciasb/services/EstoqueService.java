package com.diego.sistemafarmaciasb.services;

import com.diego.sistemafarmaciasb.dtos.estoque.AjusteLoteDTO;
import com.diego.sistemafarmaciasb.dtos.estoque.EstoqueListaDTO;
import com.diego.sistemafarmaciasb.dtos.estoque.EstoqueSaldoDTO;
import com.diego.sistemafarmaciasb.model.exceptions.RecursoNaoEncontradoException;
import com.diego.sistemafarmaciasb.model.*;
import com.diego.sistemafarmaciasb.model.enums.TipoMovimentacao;
import com.diego.sistemafarmaciasb.repository.EstoqueRepository;
import com.diego.sistemafarmaciasb.repository.ItemRepository;
import com.diego.sistemafarmaciasb.repository.MovimentacaoRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ItemRepository itemRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public EstoqueService(EstoqueRepository estoqueRepository, ItemRepository itemRepository, MovimentacaoRepository movimentacaoRepository) {
        this.estoqueRepository = estoqueRepository;
        this.itemRepository = itemRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    /**
     * Retorna a lista de saldos de estoque (agrupado por item).
     * Usado na tela principal de Estoque (visão Mestre).
     */
    @Transactional(readOnly = true)
    public Page<EstoqueSaldoDTO> listarSaldosDeEstoque(Pageable pageable, String busca) {
        if (busca == null || busca.isBlank()) {
            return estoqueRepository.findEstoqueSaldosSemFiltro(pageable);
        } else {
            return estoqueRepository.findEstoqueSaldosComFiltro(pageable, busca);
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "lotesPorItem", key = "#itemId")
    public List<EstoqueListaDTO> listarLotesPorItem(UUID itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new RecursoNaoEncontradoException("Item com ID " + itemId + " não encontrado.");
        }
        return estoqueRepository.findByItemIdOrderByDataValidadeAsc(itemId).stream()
                .map(this::paraListaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Ajusta a quantidade e/ou data de validade de um lote específico.
     * Cria uma movimentação de ajuste para fins de auditoria.
     */
    @Transactional
    public EstoqueListaDTO ajustarLote(UUID estoqueId, AjusteLoteDTO dto) {
        Estoque estoque = estoqueRepository.findById(estoqueId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lote de estoque com ID " + estoqueId + " não encontrado."));

        int quantidadeAntiga = estoque.getQuantidade();
        int novaQuantidade = dto.novaQuantidade();
        int diferenca = novaQuantidade - quantidadeAntiga;

        estoque.setQuantidade(novaQuantidade);
        estoque.setDataValidade(dto.novaDataValidade());
        Estoque loteSalvo = estoqueRepository.save(estoque);

        // Se houve alteração na quantidade, cria a movimentação de auditoria
        if (diferenca != 0) {
            // Cria a "capa" da movimentação
            Movimentacao movimentacao = new Movimentacao();
            movimentacao.setObservacao(dto.observacao());
            movimentacao.setFuncionario(getUsuarioLogado());
            movimentacao.setTipoMovimentacao(diferenca > 0 ? TipoMovimentacao.AJUSTE_ENTRADA : TipoMovimentacao.AJUSTE_SAIDA);

            movimentacao.adicionarItem(estoque.getItem(), Math.abs(diferenca));

            movimentacaoRepository.save(movimentacao);
        }

        return paraListaDTO(loteSalvo);
    }

    private EstoqueListaDTO paraListaDTO(Estoque estoque) {
        // Esta verificação pode ser otimizada se você tiver o tipo no próprio item
        String tipoItem = (estoque.getItem().getClass().getSimpleName().equalsIgnoreCase("Medicamento")) ? "MEDICAMENTO" : "INSUMO";
        return new EstoqueListaDTO(
                estoque.getId(),
                estoque.getNumeroLote(),
                estoque.getDataValidade(),
                estoque.getQuantidade(),
                estoque.getItem().getId(),
                estoque.getItem().getNome(),
                tipoItem
        );
    }

    private Funcionario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Nenhum usuário autenticado encontrado.");
        }
        return (Funcionario) authentication.getPrincipal();
    }
}