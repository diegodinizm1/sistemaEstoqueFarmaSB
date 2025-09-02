package com.diego.sistemafarmaciasb.model;

import com.diego.sistemafarmaciasb.model.enums.TipoMovimentacao;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "movimentacoes")
// 2. Substituindo @Data por anotações mais seguras
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentacao tipoMovimentacao;

    // 1. Usando um relacionamento direto com a entidade Item
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private int quantidade;

    // 1. Usando um relacionamento direto com a entidade Setor (pode ser nulo)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id")
    private Setor setor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    // Renomeado para 'observacao' para maior clareza
    private String observacao;

    @CreationTimestamp // Ótima adição!
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataMovimentacao;
}