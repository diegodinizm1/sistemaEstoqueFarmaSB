package com.diego.sistemafarmaciasb.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "item", indexes = {
        @Index(name = "idx_item_nome", columnList = "nome"),
        @Index(name = "idx_item_descricao", columnList = "descricao_detalhada")
}) // A nova tabela única
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Estratégia de herança
@DiscriminatorColumn(name = "tipo_item", discriminatorType = DiscriminatorType.STRING) // Coluna que diferencia os tipos
// Usando anotações mais seguras que @Data para entidades
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public abstract class Item { // Note que a classe é abstrata

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "descricao_detalhada", columnDefinition = "TEXT")
    private String descricaoDetalhada;

    @Column(name = "unidade_medida")
    private String unidadeMedida;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "estoque_minimo", nullable = false)
    private int estoqueMinimo = 0;
}