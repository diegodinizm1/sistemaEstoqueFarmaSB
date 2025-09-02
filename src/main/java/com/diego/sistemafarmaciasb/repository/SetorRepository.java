package com.diego.sistemafarmaciasb.repository;

import com.diego.sistemafarmaciasb.model.Setor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SetorRepository extends JpaRepository<Setor, UUID> {
}
