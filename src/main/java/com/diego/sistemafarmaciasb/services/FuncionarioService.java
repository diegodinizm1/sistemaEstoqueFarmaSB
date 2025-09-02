package com.diego.sistemafarmaciasb.services;

import com.diego.sistemafarmaciasb.dtos.funcionarios.*;
import com.diego.sistemafarmaciasb.model.exceptions.RecursoNaoEncontradoException;
import com.diego.sistemafarmaciasb.model.exceptions.ValidacaoException;
import com.diego.sistemafarmaciasb.model.Funcionario;
import com.diego.sistemafarmaciasb.repository.FuncionarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder; // 1. Injetando o codificador de senhas

    // 2. Injeção de dependências via construtor
    public FuncionarioService(FuncionarioRepository funcionarioRepository, PasswordEncoder passwordEncoder) {
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<FuncionarioListaDTO> listarTodos() {
        return funcionarioRepository.findAll().stream()
                .map(this::paraListaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public FuncionarioListaDTO criar(FuncionarioCreateDTO dto) {
        // 1. Pega o administrador que está fazendo a requisição
        Funcionario adminLogado = getUsuarioLogado();

        // 2. Verifica se a senha de confirmação fornecida bate com a senha do admin logado
        if (!passwordEncoder.matches(dto.senhaAdminConfirmacao(), adminLogado.getSenha())) {
            throw new ValidacaoException("Senha do administrador incorreta. Criação de usuário não autorizada.");
        }

        // 3. O resto da lógica continua a mesma
        if (funcionarioRepository.findByLogin(dto.login()).isPresent()) {
            throw new ValidacaoException("Login já está em uso.");
        }
        Funcionario novoFuncionario = new Funcionario();
        novoFuncionario.setNome(dto.nome());
        novoFuncionario.setLogin(dto.login());
        novoFuncionario.setSenha(passwordEncoder.encode(dto.senha()));
        novoFuncionario.setAtivo(true);

        return paraListaDTO(funcionarioRepository.save(novoFuncionario));
    }

    private Funcionario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Nenhum usuário autenticado encontrado.");
        }
        return (Funcionario) authentication.getPrincipal();
    }

    @Transactional
    public FuncionarioListaDTO atualizar(UUID id, FuncionarioUpdateDTO dto) {
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário com ID " + id + " не найден."));

        // Validação do Login: verifica se o login mudou e se o novo já está em uso
        if (!funcionario.getLogin().equals(dto.login())) {
            if (funcionarioRepository.findByLogin(dto.login()).isPresent()) {
                throw new ValidacaoException("O novo login '" + dto.login() + "' já está em uso.");
            }
            funcionario.setLogin(dto.login());
        }

        // Atualização dos outros campos
        funcionario.setNome(dto.nome());
        funcionario.setAtivo(dto.ativo());

        // Atualização condicional da senha
        if (dto.senha() != null && !dto.senha().isBlank()) {
            funcionario.setSenha(passwordEncoder.encode(dto.senha()));
        }

        Funcionario salvo = funcionarioRepository.save(funcionario);
        return paraListaDTO(salvo);
    }

    // --- Método de conversão para o DTO de listagem (seguro) ---
    private FuncionarioListaDTO paraListaDTO(Funcionario funcionario) {
        return new FuncionarioListaDTO(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getLogin(),
                funcionario.isAtivo()
        );
    }

    @Transactional
    public UsuarioDTO atualizarPerfil(PerfilUpdateDTO dto) {
        Funcionario usuarioLogado = getUsuarioLogado(); // Pega o usuário do token

        // Valida se o novo login já não está em uso por OUTRO usuário
        Optional<Funcionario> funcionarioComNovoLogin = funcionarioRepository.findByLogin(dto.login());
        if (funcionarioComNovoLogin.isPresent() && !funcionarioComNovoLogin.get().getId().equals(usuarioLogado.getId())) {
            throw new ValidacaoException("Login já está em uso.");
        }

        usuarioLogado.setNome(dto.nome());
        usuarioLogado.setLogin(dto.login());
        funcionarioRepository.save(usuarioLogado);

        return new UsuarioDTO(usuarioLogado.getId(), usuarioLogado.getNome(), usuarioLogado.getLogin());
    }

    @Transactional
    public void alterarSenha(AlterarSenhaDTO dto) {
        Funcionario usuarioLogado = getUsuarioLogado();

        // 1. Verifica se a senha atual fornecida está correta
        if (!passwordEncoder.matches(dto.senhaAtual(), usuarioLogado.getSenha())) {
            throw new ValidacaoException("A senha atual está incorreta.");
        }

        // 2. Criptografa e salva a nova senha
        usuarioLogado.setSenha(passwordEncoder.encode(dto.novaSenha()));
        funcionarioRepository.save(usuarioLogado);
    }
}