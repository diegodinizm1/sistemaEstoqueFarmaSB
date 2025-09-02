package com.diego.sistemafarmaciasb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Configuracao {
    @Id
    private String chave; // Ex: "DIAS_ALERTA_VENCIMENTO"
    private String valor; // Ex: "30"
}