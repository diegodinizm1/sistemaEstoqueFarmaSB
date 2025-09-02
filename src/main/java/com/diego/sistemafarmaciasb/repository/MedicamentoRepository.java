package com.diego.sistemafarmaciasb.repository;

import com.diego.sistemafarmaciasb.dtos.medicamentos.MedicamentoDTO;
import com.diego.sistemafarmaciasb.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MedicamentoRepository extends JpaRepository<Medicamento, UUID> {
}
