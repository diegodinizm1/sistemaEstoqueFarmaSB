package com.diego.sistemafarmaciasb.services;

import com.diego.sistemafarmaciasb.dtos.movimentacoes.MovimentacaoDetalhesDTO;
import com.diego.sistemafarmaciasb.dtos.movimentacoes.MovimentacaoEntradaDTO;
import com.diego.sistemafarmaciasb.dtos.movimentacoes.MovimentacaoHistoricoDTO;
import com.diego.sistemafarmaciasb.dtos.movimentacoes.MovimentacaoSaidaDTO;
import com.diego.sistemafarmaciasb.model.exceptions.RecursoNaoEncontradoException;
import com.diego.sistemafarmaciasb.model.exceptions.ValidacaoException;
import com.diego.sistemafarmaciasb.model.*;
import com.diego.sistemafarmaciasb.model.enums.TipoMovimentacao;
import com.diego.sistemafarmaciasb.repository.EstoqueRepository;
import com.diego.sistemafarmaciasb.repository.ItemRepository;
import com.diego.sistemafarmaciasb.repository.MovimentacaoRepository;
import com.diego.sistemafarmaciasb.repository.SetorRepository;
import org.hibernate.Hibernate;
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
    private final EstoqueRepository estoqueRepository; // Novo repositório

    public MovimentacaoService(MovimentacaoRepository mRepo, ItemRepository iRepo, SetorRepository sRepo, EstoqueRepository eRepo) {
        this.movimentacaoRepository = mRepo;
        this.itemRepository = iRepo;
        this.setorRepository = sRepo;
        this.estoqueRepository = eRepo;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "historicoMovimentacoes")
    public List<MovimentacaoHistoricoDTO> listarHistorico() {
        return movimentacaoRepository.findAllByOrderByDataMovimentacaoDesc().stream()
                .map(this::paraHistoricoDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "historicoMovimentacoes", allEntries = true)
    public MovimentacaoHistoricoDTO registrarEntrada(MovimentacaoEntradaDTO dto) {
        Item item = itemRepository.findById(dto.itemId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item não encontrado."));

        // 1. Cria um novo registro de lote no estoque
        Estoque novoLote = new Estoque();
        novoLote.setItem(item);
        novoLote.setNumeroLote(dto.numeroLote());
        novoLote.setDataValidade(dto.dataValidade());
        novoLote.setQuantidade(dto.quantidade());
        estoqueRepository.save(novoLote);

        // 2. Cria a movimentação para o histórico
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setItem(item);
        movimentacao.setQuantidade(dto.quantidade());
        movimentacao.setObservacao(dto.observacao());
        movimentacao.setFuncionario(getUsuarioLogado());
        movimentacao.setTipoMovimentacao(TipoMovimentacao.ENTRADA);

        Movimentacao movimentacaoSalva = movimentacaoRepository.save(movimentacao);
        return paraHistoricoDTO(movimentacaoSalva);
    }

    @Transactional
    @CacheEvict(value = "historicoMovimentacoes", allEntries = true)
    public MovimentacaoHistoricoDTO registrarSaida(MovimentacaoSaidaDTO dto) {
        Item item = itemRepository.findById(dto.itemId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item não encontrado."));
        Setor setor = setorRepository.findById(dto.setorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Setor de destino não encontrado."));

        // 1. Busca todos os lotes disponíveis para o item, ordenados por validade (FEFO)
        List<Estoque> lotesDisponiveis = estoqueRepository.findByItemIdAndQuantidadeGreaterThanOrderByDataValidadeAsc(item.getId(), 0);

        // 2. Verifica se o estoque total é suficiente
        int estoqueTotal = lotesDisponiveis.stream().mapToInt(Estoque::getQuantidade).sum();
        if (estoqueTotal < dto.quantidade()) {
            throw new ValidacaoException("Estoque insuficiente. Total disponível: " + estoqueTotal);
        }

        // 3. Lógica FEFO (First-Expired, First-Out) para dar baixa nos lotes
        int quantidadePendente = dto.quantidade();
        List<Estoque> lotesModificados = new ArrayList<>();

        for (Estoque lote : lotesDisponiveis) {
            if (quantidadePendente <= 0) break;

            int quantidadeARetirar = Math.min(quantidadePendente, lote.getQuantidade());
            lote.setQuantidade(lote.getQuantidade() - quantidadeARetirar);
            quantidadePendente -= quantidadeARetirar;
            lotesModificados.add(lote);
        }
        estoqueRepository.saveAll(lotesModificados);

        // 4. Cria a movimentação para o histórico
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setItem(item);
        movimentacao.setSetor(setor);
        movimentacao.setQuantidade(dto.quantidade());
        movimentacao.setObservacao(dto.observacao());
        movimentacao.setFuncionario(getUsuarioLogado());
        movimentacao.setTipoMovimentacao(TipoMovimentacao.SAIDA);

        Movimentacao movimentacaoSalva = movimentacaoRepository.save(movimentacao);
        return paraHistoricoDTO(movimentacaoSalva);
    }


    private MovimentacaoHistoricoDTO paraHistoricoDTO(Movimentacao mov) {
        Item item = mov.getItem();
        String tipoItem = (item instanceof Medicamento) ? "MEDICAMENTO" : "INSUMO";
        return new MovimentacaoHistoricoDTO(
                mov.getId(),
                mov.getTipoMovimentacao(),
                mov.getItem().getNome(),
                mov.getQuantidade(),
                mov.getSetor() != null ? mov.getSetor().getNome() : null,
                mov.getObservacao(),
                mov.getDataMovimentacao(),
                tipoItem
        );
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "detalhesMovimentacao", key = "#id")
    public MovimentacaoDetalhesDTO buscarDetalhesPorId(UUID id) {
        Movimentacao mov = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Movimentação com ID " + id + " não encontrada."));

        // Converte a entidade para o DTO de detalhes
        return paraDetalhesDTO(mov);
    }

    // Novo método de conversão
    private MovimentacaoDetalhesDTO paraDetalhesDTO(Movimentacao mov) {
        Item item = mov.getItem();
        Object itemReal = Hibernate.unproxy(item);
        String tipoItem = (itemReal instanceof Medicamento) ? "MEDICAMENTO" : "INSUMO";

        return new MovimentacaoDetalhesDTO(
                mov.getId(),
                mov.getTipoMovimentacao(),
                mov.getDataMovimentacao(),
                mov.getObservacao(),
                mov.getItem().getNome(),
                tipoItem,
                mov.getFuncionario().getNome(),
                mov.getQuantidade(),
                mov.getSetor() != null ? mov.getSetor().getNome() : null
        );
    }

    private Funcionario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Nenhum usuário autenticado encontrado.");
        }
        // Faz o cast do 'principal' para a sua entidade Funcionario
        return (Funcionario) authentication.getPrincipal();
    }
}