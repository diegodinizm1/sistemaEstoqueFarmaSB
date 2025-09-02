package com.diego.sistemafarmaciasb.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("MEDICAMENTO") // Valor na coluna 'tipo_item' para esta classe
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medicamento extends Item { // Herda de Item

    @Enumerated(EnumType.STRING)
    private com.diego.sistemafarmaciasb.model.MedicamentoTipo tipo;

}