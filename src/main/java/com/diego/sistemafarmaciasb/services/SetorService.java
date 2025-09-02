package com.diego.sistemafarmaciasb.services;

import com.diego.sistemafarmaciasb.dtos.setor.SetorDTO;
import com.diego.sistemafarmaciasb.model.Setor;
import com.diego.sistemafarmaciasb.repository.SetorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SetorService {

    @Autowired
    private SetorRepository setorRepository;

    public List<SetorDTO> listarTodos() {
        return setorRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<SetorDTO> buscarPorId(UUID id) {
        return setorRepository.findById(id)
                .map(this::convertToDto);
    }

    public SetorDTO salvar(SetorDTO dto) {
        Setor setor = convertToEntity(dto);
        Setor salvo = setorRepository.save(setor);
        return convertToDto(salvo);
    }

    private SetorDTO convertToDto(Setor setor) {
        return new SetorDTO(setor.getId(), setor.getNome());
    }

    private Setor convertToEntity(SetorDTO dto) {
        Setor setor = new Setor();
        setor.setId(dto.id());
        setor.setNome(dto.nome());
        return setor;
    }
}