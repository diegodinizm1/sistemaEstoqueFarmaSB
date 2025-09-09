package com.diego.sistemafarmaciasb.model;

import com.diego.sistemafarmaciasb.model.enums.TipoMovimentacao;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representa o evento de movimentação geral (a "capa" da requisição).
 * Esta é a entidade "mãe" no relacionamento Um-para-Muitos.
 */
@Entity
@Table(name = "movimentacoes", indexes = {
        @Index(name = "idx_movimentacao_setor_id", columnList = "setor_id"),
        @Index(name = "idx_mov_data_setor", columnList = "data_movimentacao, setor_id")
})
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

    /**
     * UMA movimentação agora tem MUITOS itens.
     * cascade = CascadeType.ALL: Salvar/deletar a Movimentacao irá salvar/deletar os itens filhos automaticamente.
     * orphanRemoval = true: Remover um item da lista irá deletá-lo do banco ao salvar.
     */
    @OneToMany(mappedBy = "movimentacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MovimentacaoItem> itens = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id")
    private Setor setor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    private String observacao;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataMovimentacao;

    // --- MÉTODOS AUXILIARES ---

    /**
     * Método prático para adicionar um item à lista de movimentação,
     * garantindo que a relação bidirecional seja mantida.
     * @param item O Item a ser adicionado.
     * @param quantidade A quantidade do item.
     */
    public void adicionarItem(Item item, int quantidade) {
        MovimentacaoItem novoMovimentacaoItem = new MovimentacaoItem();
        novoMovimentacaoItem.setItem(item);
        novoMovimentacaoItem.setQuantidade(quantidade);
        novoMovimentacaoItem.setMovimentacao(this); // Link de volta para esta movimentação
        this.itens.add(novoMovimentacaoItem);
    }
}