package com.diego.sistemafarmaciasb.repository;

import com.diego.sistemafarmaciasb.model.Configuracao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ConfiguracaoRepository extends JpaRepository<Configuracao, String> {
}
