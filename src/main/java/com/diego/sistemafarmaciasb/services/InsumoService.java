package com.diego.sistemafarmaciasb.services;

import com.diego.sistemafarmaciasb.dtos.insumos.InsumoDTO;
import com.diego.sistemafarmaciasb.model.Insumo;
import com.diego.sistemafarmaciasb.model.exceptions.RecursoNaoEncontradoException;
import com.diego.sistemafarmaciasb.repository.InsumoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InsumoService {

    @Autowired
    private InsumoRepository insumoRepository;

    @Cacheable(value = "insumos")
    public List<InsumoDTO> listarTodos() {
        return insumoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "insumos", allEntries = true)
    public InsumoDTO atualizar(UUID id, InsumoDTO dto) {
        // 1. Busca o insumo ou lança uma exceção 404 se não encontrar.
        Insumo insumo = insumoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo com ID " + id + " não encontrado."));

        // 2. Atualiza os campos da entidade com os dados do DTO.
        insumo.setNome(dto.nome());
        insumo.setDescricaoDetalhada(dto.descricaoDetalhada());
        insumo.setUnidadeMedida(dto.unidadeMedida());

        // 3. Salva a entidade atualizada.
        Insumo insumoSalvo = insumoRepository.save(insumo);

        // 4. Retorna o DTO correspondente à entidade salva.
        return convertToDto(insumoSalvo);
    }

    // --- MÉTODO DE EXCLUSÃO ---
    @Transactional
    @CacheEvict(value = "insumos", allEntries = true)
    public void deletar(UUID id) {
        Insumo insumo = insumoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Insumo com ID " + id + " não encontrado para exclusão."));

        // 2. Se existe, deleta o insumo.
        insumoRepository.delete(insumo);
    }

    @CacheEvict(value = "insumos", allEntries = true)
    public InsumoDTO salvar(InsumoDTO dto) {
        Insumo insumo = convertToEntity(dto);
        Insumo salvo = insumoRepository.save(insumo);
        return convertToDto(salvo);
    }

    // Métodos de conversão
    private InsumoDTO convertToDto(Insumo insumo) {
        return new InsumoDTO(
                insumo.getId(),
                insumo.getNome(),
                insumo.getDescricaoDetalhada(),
                insumo.getUnidadeMedida()
        );
    }

    private Insumo convertToEntity(InsumoDTO dto) {
        Insumo insumo = new Insumo();
        insumo.setId(dto.id());
        insumo.setNome(dto.nome());
        insumo.setDescricaoDetalhada(dto.descricaoDetalhada());
        insumo.setUnidadeMedida(dto.unidadeMedida());
        return insumo;
    }

    @Cacheable(value = "insumoPorId", key = "#id")
    public Optional<InsumoDTO> buscarPorId(UUID id) {
        return insumoRepository.findById(id).map(this::convertToDto);
    }
}