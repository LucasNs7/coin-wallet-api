package com.lucas.coin_wallet_api.service;

import com.lucas.coin_wallet_api.client.AwesomeApiClient;
import com.lucas.coin_wallet_api.client.AwesomeApiCotacaoResponse;
import com.lucas.coin_wallet_api.dto.CarteiraRequest;
import com.lucas.coin_wallet_api.dto.CarteiraResponse;
import com.lucas.coin_wallet_api.dto.PatrimonioTotalResponse;
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
        Usuario usuario = usuarioService.buscaUsuarioPorEmail(request.usuarioEmail());

        Carteira carteira = carteiraRepository.findByUsuarioAndMoeda(usuario, request.moeda())
                .orElseGet(() -> carteiraMapper.toEntity(request, usuario));

        if (carteira.getId() != null) {
            carteira.setQuantidade(carteira.getQuantidade().add(request.quantidade()));
        }

        return carteiraMapper.toResponse(carteiraRepository.save(carteira));
    }

    @Transactional
    public PatrimonioTotalResponse calcularTotalEmBrl(String email) {
        Usuario usuario = usuarioService.buscaUsuarioPorEmail(email);

        List<Carteira> carteiras = carteiraRepository.findAllByUsuario(usuario);

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
                usuario.getEmail(),
                respostas,
                total.setScale(4, RoundingMode.HALF_EVEN)
        );
    }

    private BigDecimal buscarCotacaoParaBrl(Moeda moeda) {
        String par = moeda.name() + "-BRL";
        String chaveResposta = moeda.name() + "BRL";

        try {
            Map<String, AwesomeApiCotacaoResponse> resposta = awesomeApiClient.buscarCotacao(par);

            AwesomeApiCotacaoResponse cotacao = resposta == null ? null : resposta.get(chaveResposta);

            if (cotacao == null || cotacao.bid() == null) {
                throw new CotacaoIndisponivelException("Cotação indisponível para " + par + ".");
            }

            return cotacao.bid();

        } catch (CotacaoIndisponivelException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new CotacaoIndisponivelException("Não foi possível consultar a cotação de " + par + ".");
        }
    }
}
