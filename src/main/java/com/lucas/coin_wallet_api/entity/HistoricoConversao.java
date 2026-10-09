package com.lucas.coin_wallet_api.entity;

import com.lucas.coin_wallet_api.Enum.Moeda;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tb_historico_conversao")
public class HistoricoConversao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Moeda moedaOrigem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Moeda moedaDestino;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal valorOriginal;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal cotacaoAplicada;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal valorConvertido;

    @Column(nullable = false)
    private LocalDateTime dataConsulta;
}
