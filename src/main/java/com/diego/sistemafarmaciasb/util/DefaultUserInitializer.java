package com.diego.sistemafarmaciasb.util;

import com.diego.sistemafarmaciasb.model.Funcionario;
import com.diego.sistemafarmaciasb.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserInitializer implements CommandLineRunner {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (funcionarioRepository.findByLogin("admin").isEmpty()) {
            Funcionario adminUser = new Funcionario();
            adminUser.setNome("Administrador");
            adminUser.setLogin("admin");

            // Criptografa a senha antes de salvar
            String encryptedPassword = passwordEncoder.encode("admin123");
            adminUser.setSenha(encryptedPassword);

            // Salva o usuário no banco de dados
            funcionarioRepository.save(adminUser);
            System.out.println("Usuário padrão 'admin' criado com sucesso!");
        }
    }
}