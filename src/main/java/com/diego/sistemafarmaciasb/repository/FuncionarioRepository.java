package com.diego.sistemafarmaciasb.repository;

import com.diego.sistemafarmaciasb.dtos.funcionarios.FuncionarioDTO;
import com.diego.sistemafarmaciasb.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.util.UUID;

public interface FuncionarioRepository extends JpaRepository<Funcionario, UUID> {
    Optional<Funcionario> findByLogin (String username);
}
