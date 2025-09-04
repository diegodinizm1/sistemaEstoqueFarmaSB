package com.diego.sistemafarmaciasb.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "estoque", indexes = {
        @Index(name = "idx_estoque_item_validade", columnList = "item_id, data_validade"),
        @Index(name = "idx_estoque_item_quantidade", columnList = "item_id, quantidade")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // 1. UM ÚNICO RELACIONAMENTO com a entidade base Item
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "numero_lote", nullable = false)
    private String numeroLote;

    @Column(name = "data_validade", nullable = false)
    private LocalDate dataValidade;

    @Column(nullable = false)
    private int quantidade;
}