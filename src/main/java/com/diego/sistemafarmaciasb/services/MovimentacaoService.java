package com.diego.sistemafarmaciasb.services;

import com.diego.sistemafarmaciasb.dtos.movimentacoes.*;
import com.diego.sistemafarmaciasb.model.exceptions.RecursoNaoEncontradoException;
import com.diego.sistemafarmaciasb.model.exceptions.ValidacaoException;
import com.diego.sistemafarmaciasb.model.*;
import com.diego.sistemafarmaciasb.model.enums.TipoMovimentacao;
import com.diego.sistemafarmaciasb.repository.EstoqueRepository;
import com.diego.sistemafarmaciasb.repository.ItemRepository;
import com.diego.sistemafarmaciasb.repository.MovimentacaoRepository;
import com.diego.sistemafarmaciasb.repository.SetorRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ItemRepository itemRepository;
    private final SetorRepository setorRepository;
    private final EstoqueRepository estoqueRepository;

    @Autowired
    public MovimentacaoService(MovimentacaoRepository mRepo, ItemRepository iRepo, SetorRepository sRepo, EstoqueRepository eRepo) {
        this.movimentacaoRepository = mRepo;
        this.itemRepository = iRepo;
        this.setorRepository = sRepo;
        this.estoqueRepository = eRepo;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "historicoMovimentacoes")
    public List<MovimentacaoHistoricoDTO> listarHistorico() {
        return movimentacaoRepository.findAllByOrderByDataMovimentacaoDesc().stream().map(this::paraHistoricoDTO).collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "historicoMovimentacoes", allEntries = true)
    public MovimentacaoDetalhesDTO registrarEntrada(MovimentacaoEntradaDTO dto) {
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setTipoMovimentacao(TipoMovimentacao.ENTRADA);
        movimentacao.setObservacao(dto.observacao());
        movimentacao.setFuncionario(getUsuarioLogado());

        for (ItemEntradaDTO itemDto : dto.itens()) {
            Item item = itemRepository.findById(itemDto.itemId()).orElseThrow(() -> new RecursoNaoEncontradoException("Item com ID " + itemDto.itemId() + " não encontrado."));

            Estoque novoLote = new Estoque(null, item, itemDto.numeroLote(), itemDto.dataValidade(), itemDto.quantidade());
            estoqueRepository.save(novoLote);

            movimentacao.adicionarItem(item, itemDto.quantidade());
        }

        // 3. Salva a movimentação mãe UMA ÚNICA VEZ (o Cascade salva os filhos)
        Movimentacao movimentacaoSalva = movimentacaoRepository.save(movimentacao);
        return paraDetalhesDTO(movimentacaoSalva);
    }

    @Transactional
    @CacheEvict(value = "historicoMovimentacoes", allEntries = true)
    public MovimentacaoDetalhesDTO registrarSaida(MovimentacaoSaidaDTO dto) {
        Setor setor = setorRepository.findById(dto.setorId()).orElseThrow(() -> new RecursoNaoEncontradoException("Setor de destino não encontrado."));

        // 1. Cria a "capa" da movimentação
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setTipoMovimentacao(TipoMovimentacao.SAIDA);
        movimentacao.setObservacao(dto.observacao());
        movimentacao.setFuncionario(getUsuarioLogado());
        movimentacao.setSetor(setor);

        // 2. Itera sobre cada item do DTO
        for (ItemSaidaDTO itemDto : dto.itens()) {
            Item item = itemRepository.findById(itemDto.itemId()).orElseThrow(() -> new RecursoNaoEncontradoException("Item com ID " + itemDto.itemId() + " não encontrado."));

            // 2.1. Executa a lógica de baixa de estoque para o item atual
            darBaixaEstoqueFEFO(item, itemDto.quantidade());

            // 2.2. Adiciona o item à movimentação mãe
            movimentacao.adicionarItem(item, itemDto.quantidade());
        }

        // 3. Salva a movimentação mãe UMA ÚNICA VEZ
        Movimentacao movimentacaoSalva = movimentacaoRepository.save(movimentacao);
        return paraDetalhesDTO(movimentacaoSalva);
    }

    // Método privado para encapsular a lógica de baixa FEFO
    private void darBaixaEstoqueFEFO(Item item, int quantidadeParaRetirar) {
        List<Estoque> lotesDisponiveis = estoqueRepository.findByItemIdAndQuantidadeGreaterThanOrderByDataValidadeAsc(item.getId(), 0);
        int estoqueTotal = lotesDisponiveis.stream().mapToInt(Estoque::getQuantidade).sum();

        if (estoqueTotal < quantidadeParaRetirar) {
            throw new ValidacaoException("Estoque insuficiente para o item '" + item.getNome() + "'. Total disponível: " + estoqueTotal);
        }

        int quantidadePendente = quantidadeParaRetirar;
        List<Estoque> lotesModificados = new ArrayList<>();

        for (Estoque lote : lotesDisponiveis) {
            if (quantidadePendente <= 0) break;
            int quantidadeDoLote = Math.min(quantidadePendente, lote.getQuantidade());
            lote.setQuantidade(lote.getQuantidade() - quantidadeDoLote);
            quantidadePendente -= quantidadeDoLote;
            lotesModificados.add(lote);
        }
        estoqueRepository.saveAll(lotesModificados);
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "detalhesMovimentacao", key = "#id")
    public MovimentacaoDetalhesDTO buscarDetalhesPorId(UUID id) {
        Movimentacao mov = movimentacaoRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Movimentação com ID " + id + " não encontrada."));
        return paraDetalhesDTO(mov);
    }

    // Conversores de DTO
    private MovimentacaoDetalhesDTO paraDetalhesDTO(Movimentacao mov) {
        List<ItemMovimentadoDTO> itemDTOs = mov.getItens().stream().map(movItem -> {
            Item item = movItem.getItem();
            Object itemReal = Hibernate.unproxy(item);
            String tipoItem = (itemReal instanceof Medicamento) ? "MEDICAMENTO" : "INSUMO";
            return new ItemMovimentadoDTO(item.getNome(), tipoItem, movItem.getQuantidade());
        }).collect(Collectors.toList());

        return new MovimentacaoDetalhesDTO(mov.getId(), mov.getTipoMovimentacao(), mov.getDataMovimentacao(), mov.getObservacao(), mov.getFuncionario().getNome(), mov.getSetor() != null ? mov.getSetor().getNome() : null, itemDTOs);
    }

    private MovimentacaoHistoricoDTO paraHistoricoDTO(Movimentacao mov) {
        return new MovimentacaoHistoricoDTO(mov.getId(), mov.getTipoMovimentacao(), mov.getItens().size(), mov.getItens().stream().mapToInt(MovimentacaoItem::getQuantidade).sum(), mov.getSetor() != null ? mov.getSetor().getNome() : null, mov.getObservacao(), mov.getDataMovimentacao(), mov.getFuncionario().getNome());
    }

    private Funcionario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Nenhum usuário autenticado encontrado.");
        }
        return (Funcionario) authentication.getPrincipal();
    }
}