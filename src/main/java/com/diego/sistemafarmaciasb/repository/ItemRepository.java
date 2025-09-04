package com.diego.sistemafarmaciasb.repository;

import com.diego.sistemafarmaciasb.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID>, JpaSpecificationExecutor<Item> {
    // Graças à herança, este findAll() busca tanto Medicamentos quanto Insumos.

    @Query("SELECT count(i) FROM Item i WHERE TYPE(i) = Medicamento")
    long countMedicamentos();

    @Query("SELECT count(i) FROM Item i WHERE TYPE(i) = Insumo")
    long countInsumos();

    @Query("SELECT COUNT(DISTINCT e.item) FROM Estoque e WHERE TYPE(e.item) = Medicamento")
    long countMedicamentosComEstoque();

    // NOVO: Conta quantos insumos distintos existem na tabela de estoque
    @Query("SELECT COUNT(DISTINCT e.item) FROM Estoque e WHERE TYPE(e.item) = Insumo")
    long countInsumosComEstoque();

}