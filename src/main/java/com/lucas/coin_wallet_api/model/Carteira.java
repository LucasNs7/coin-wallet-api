package com.lucas.coin_wallet_api.model;

import com.lucas.coin_wallet_api.model.enums.Moeda;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "moeda"}))
public class Carteira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id",  nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Moeda moeda;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantidade;
}
