package com.diego.sistemafarmaciasb.repository;

import com.diego.sistemafarmaciasb.model.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InsumoRepository extends JpaRepository<Insumo, UUID> {
}
