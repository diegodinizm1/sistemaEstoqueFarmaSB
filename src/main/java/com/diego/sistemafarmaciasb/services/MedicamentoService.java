package com.diego.sistemafarmaciasb.services;

import com.diego.sistemafarmaciasb.dtos.medicamentos.MedicamentoDTO;
import com.diego.sistemafarmaciasb.model.Medicamento;
import com.diego.sistemafarmaciasb.model.exceptions.RecursoNaoEncontradoException;
import com.diego.sistemafarmaciasb.model.exceptions.ResourceInUseException;
import com.diego.sistemafarmaciasb.repository.MedicamentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    public List<MedicamentoDTO> listarTodos() {
        return medicamentoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public MedicamentoDTO salvar(MedicamentoDTO dto) {
        Medicamento medicamento = convertToEntity(dto);
        Medicamento salvo = medicamentoRepository.save(medicamento);
        return convertToDto(salvo);
    }

    @Transactional
    public MedicamentoDTO atualizar(UUID id, MedicamentoDTO dto) {
        Medicamento medicamentoAntigo = medicamentoRepository.findById(id).orElseThrow(
                ()-> new RecursoNaoEncontradoException("Medicamento não encontrado")
        );
        if(medicamentoAntigo != null) {
            medicamentoAntigo.setNome(dto.nome());
            medicamentoAntigo.setTipo(dto.tipo());
            medicamentoAntigo.setDescricaoDetalhada(dto.descricaoDetalhada());
            medicamentoAntigo.setUnidadeMedida(dto.unidadeMedida());
            medicamentoAntigo.setAtivo(true);
            medicamentoAntigo.setEstoqueMinimo(dto.estoqueMinimo());
            medicamentoRepository.save(medicamentoAntigo);
            return convertToDto(medicamentoAntigo);
        }
        throw new RuntimeException("Medicamento inexistente");
    }

    // Métodos de conversão
    private MedicamentoDTO convertToDto(Medicamento medicamento) {
        return new MedicamentoDTO(
                medicamento.getId(),
                medicamento.getNome(),
                medicamento.getDescricaoDetalhada(),
                medicamento.getUnidadeMedida(),
                medicamento.getTipo(),
                medicamento.getEstoqueMinimo()
        );
    }

    private Medicamento convertToEntity(MedicamentoDTO dto) {
        Medicamento medicamento = new Medicamento();
        medicamento.setId(dto.id());
        medicamento.setNome(dto.nome());
        medicamento.setDescricaoDetalhada(dto.descricaoDetalhada());
        medicamento.setUnidadeMedida(dto.unidadeMedida());
        medicamento.setTipo(dto.tipo());
        return medicamento;
    }

    public Optional<MedicamentoDTO> buscarPorId(UUID id) {
        return medicamentoRepository.findById(id).map(this::convertToDto);
    }

    public void removerPorId(UUID id) {
        if(medicamentoRepository.existsById(id)) {
            try {
                medicamentoRepository.deleteById(id);
            } catch(DataIntegrityViolationException e){
                if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
                    String sqlState = ((org.hibernate.exception.ConstraintViolationException) e.getCause()).getSQLException().getSQLState();

                    if ("23503".equals(sqlState)) {
                        throw new ResourceInUseException("Este item não pode ser excluído, pois está sendo usado em outros registros.");
                    }
                }
            }
        }else{
            throw new RecursoNaoEncontradoException("Medicamento inexistente para deleção");
        }
    }
}