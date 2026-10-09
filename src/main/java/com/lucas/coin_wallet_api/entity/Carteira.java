package com.lucas.coin_wallet_api.entity;

import com.lucas.coin_wallet_api.Enum.Moeda;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "tb_carteira")
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
