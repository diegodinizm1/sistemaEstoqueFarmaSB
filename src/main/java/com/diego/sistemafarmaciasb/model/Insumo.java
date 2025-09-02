package com.diego.sistemafarmaciasb.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("INSUMO") // Valor na coluna 'tipo_item' para esta classe
@NoArgsConstructor
public class Insumo extends Item { // Herda de Item
}