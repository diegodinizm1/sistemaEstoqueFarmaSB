package com.diego.sistemafarmaciasb.services;

import com.diego.sistemafarmaciasb.model.Configuracao;
import com.diego.sistemafarmaciasb.repository.ConfiguracaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConfiguracaoService {

    private final ConfiguracaoRepository configuracaoRepository;

    public ConfiguracaoService(ConfiguracaoRepository configuracaoRepository) {
        this.configuracaoRepository = configuracaoRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, String> getTodasConfiguracoes() {
        return configuracaoRepository.findAll().stream()
                .collect(Collectors.toMap(Configuracao::getChave, Configuracao::getValor));
    }

    @Transactional
    public void salvarConfiguracoes(Map<String, String> configuracoes) {
        configuracoes.forEach((chave, valor) -> {
            Configuracao config = configuracaoRepository.findById(chave)
                    .orElse(new Configuracao(chave, valor));
            config.setValor(valor);
            configuracaoRepository.save(config);
        });
    }

    // Método auxiliar para buscar um valor com um padrão
    public String getValor(String chave, String valorPadrao) {
        return configuracaoRepository.findById(chave)
                .map(Configuracao::getValor)
                .orElse(valorPadrao);
    }
}