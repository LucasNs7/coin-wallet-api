package com.lucas.coin_wallet_api.service;

import com.lucas.coin_wallet_api.client.AwesomeApiClient;
import com.lucas.coin_wallet_api.client.AwesomeApiCotacaoResponse;
import com.lucas.coin_wallet_api.controller.dto.CarteiraRequest;
import com.lucas.coin_wallet_api.controller.dto.CarteiraResponse;
import com.lucas.coin_wallet_api.controller.dto.PatrimonioTotalResponse;
import com.lucas.coin_wallet_api.exception.CotacaoIndisponivelException;
import com.lucas.coin_wallet_api.mapper.CarteiraMapper;
import com.lucas.coin_wallet_api.model.Carteira;
import com.lucas.coin_wallet_api.model.Usuario;
import com.lucas.coin_wallet_api.model.enums.Moeda;
import com.lucas.coin_wallet_api.repository.CarteiraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CarteiraService {

    private final CarteiraRepository carteiraRepository;
    private final UsuarioService usuarioService;
    private final CarteiraMapper carteiraMapper;
    private final AwesomeApiClient awesomeApiClient;

    public CarteiraResponse criarOuAtualizar(CarteiraRequest request) {
        String userEmail = request.usuarioEmail();

        Carteira carteira = carteiraRepository.findByUsuarioEmailAndMoeda(userEmail, request.moeda())
                .orElseGet(() -> {
                    Usuario usuario = usuarioService.buscaUsuarioPorEmail(userEmail);
                    return carteiraMapper.toEntity(request, usuario);
                });

        if (carteira.getId() != null) {
            carteira.setQuantidade(carteira.getQuantidade().add(request.quantidade()));
        }

        return carteiraMapper.toResponse(carteiraRepository.save(carteira));
    }

    @Transactional
    public PatrimonioTotalResponse calcularTotalEmBrl(String email) {
        // Spring Security garantiria email válido com Usuário existente e autenticado.
        // Retorna lista vazia e total zero se o usuário não tiver carteiras.
        List<Carteira> carteiras = carteiraRepository.findAllByUsuarioEmail(email);

        BigDecimal total = BigDecimal.ZERO;

        List<CarteiraResponse> respostas = new ArrayList<>();

        for (Carteira carteira : carteiras) {
            respostas.add(carteiraMapper.toResponse(carteira));

            if (carteira.getMoeda() == Moeda.BRL) {
                total = total.add(carteira.getQuantidade());
            } else {
                BigDecimal cotacao = buscarCotacaoParaBrl(carteira.getMoeda());

                BigDecimal valorEmBrl = carteira.getQuantidade().multiply(cotacao);

                total = total.add(valorEmBrl);
            }
        }

        return new PatrimonioTotalResponse(
                email,
                respostas,
                total.setScale(4, RoundingMode.HALF_EVEN)
        );
    }

    /**<p><b>parDeMoedas:</b> Indica a relação de troca entre duas moedas.<br>
     * <b>Ex:</b> {@code USD-BRL} -> "Quanto custa 1 Dólar em Reais?".</p>
     * @return O valor do {@code bid} (preço atual de compra) da moeda em Reais.
     * @throws CotacaoIndisponivelException para erros genéricos da transação entre APIs.
     */
    private BigDecimal buscarCotacaoParaBrl(Moeda moeda) {
        String parDeMoedas = moeda.name() + "-BRL";
        String chaveResposta = moeda.name() + "BRL";

        try {
            Map<String, AwesomeApiCotacaoResponse> resposta =
                    awesomeApiClient.buscarCotacao(parDeMoedas);

            AwesomeApiCotacaoResponse cotacao =
                    (resposta != null) ? resposta.get(chaveResposta) : null;

            if (cotacao == null || cotacao.bid() == null) {
                throw new CotacaoIndisponivelException(
                        "Cotação indisponível para " + parDeMoedas + "."
                );
            }

            return cotacao.bid();

        } catch (CotacaoIndisponivelException e) {
            throw e;
        } catch (Exception e) {
            throw new CotacaoIndisponivelException(
                    "Não foi possível consultar a cotação de " + parDeMoedas + ".", e
            );
        }
    }
}
