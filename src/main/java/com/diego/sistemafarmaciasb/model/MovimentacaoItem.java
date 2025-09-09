package com.diego.sistemafarmaciasb.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Representa um item específico (e sua quantidade) dentro de uma Movimentacao.
 * Esta é a entidade "filha" no relacionamento Um-para-Muitos.
 */
@Entity
@Table(name = "movimentacao_itens", indexes = {
        // Índice para buscar rapidamente todos os itens de uma movimentação
        @Index(name = "idx_movitem_movimentacao_id", columnList = "movimentacao_id"),
        // Índice para buscar rapidamente o histórico de um item específico
        @Index(name = "idx_movitem_item_id", columnList = "item_id")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class MovimentacaoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Relacionamento de volta para a Movimentação "mãe".
    // É essencial para o `mappedBy` funcionar na outra classe.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movimentacao_id")
    private Movimentacao movimentacao;

    // O item que está sendo movimentado nesta linha.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(nullable = false)
    private int quantidade;

}