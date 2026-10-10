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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarteiraServiceTest {

    @Mock
    private CarteiraRepository carteiraRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private CarteiraMapper carteiraMapper;

    @Mock
    private AwesomeApiClient awesomeApiClient;

    @InjectMocks
    private CarteiraService carteiraService;

    private Carteira carteira;
    private CarteiraResponse response;

    @BeforeEach
    void setUp() {
        carteira = mock(Carteira.class);
        response = mock(CarteiraResponse.class);
    }

    private CarteiraRequest criarRequestBrl() {
        return new CarteiraRequest(
                "teste@email.com",
                Moeda.BRL,
                new BigDecimal("100.00")
        );
    }

    @Test
    void deveCriarNovaCarteira() {
        CarteiraRequest request = criarRequestBrl();
        Usuario usuario = mock(Usuario.class);

        when(carteiraRepository
                .findByUsuarioEmailAndMoeda("teste@email.com", Moeda.BRL))
                .thenReturn(Optional.empty());

        when(usuarioService.buscaUsuarioPorEmail("teste@email.com"))
                .thenReturn(usuario);

        when(carteiraMapper.toEntity(request, usuario))
                .thenReturn(carteira);

        when(carteira.getId()).thenReturn(null);
        when(carteiraRepository.save(carteira)).thenReturn(carteira);
        when(carteiraMapper.toResponse(carteira)).thenReturn(response);

        CarteiraResponse resultado =
                carteiraService.criarOuAtualizar(request);

        assertSame(response, resultado);

        verify(usuarioService).buscaUsuarioPorEmail("teste@email.com");
        verify(carteiraMapper).toEntity(request, usuario);
        verify(carteiraRepository).save(carteira);
        verify(carteiraMapper).toResponse(carteira);
    }

    @Test
    void deveSomarQuantidadeAoAtualizarCarteiraExistente() {
        CarteiraRequest request = criarRequestBrl();

        when(carteiraRepository
                .findByUsuarioEmailAndMoeda("teste@email.com", Moeda.BRL))
                .thenReturn(Optional.of(carteira));

        when(carteira.getId()).thenReturn(1L);
        when(carteira.getQuantidade()).thenReturn(new BigDecimal("50.00"));
        when(carteiraRepository.save(carteira)).thenReturn(carteira);
        when(carteiraMapper.toResponse(carteira)).thenReturn(response);

        CarteiraResponse resultado =
                carteiraService.criarOuAtualizar(request);

        assertSame(response, resultado);

        verify(carteira).setQuantidade(new BigDecimal("150.00"));
        verify(carteiraRepository).save(carteira);
        verify(usuarioService, never()).buscaUsuarioPorEmail(any());
        verify(carteiraMapper, never()).toEntity(any(), any());
    }

    @Test
    void deveCalcularPatrimonioSomenteComCarteiraEmBrl() {
        when(carteiraRepository.findAllByUsuarioEmail("teste@email.com"))
                .thenReturn(List.of(carteira));

        when(carteira.getMoeda()).thenReturn(Moeda.BRL);
        when(carteira.getQuantidade()).thenReturn(new BigDecimal("250.00"));
        when(carteiraMapper.toResponse(carteira)).thenReturn(response);

        PatrimonioTotalResponse resultado =
                carteiraService.calcularTotalEmBrl("teste@email.com");

        assertNotNull(resultado);
        assertEquals("teste@email.com", resultado.usuarioEmail());
        assertEquals(1, resultado.carteiras().size());
        assertEquals(
                0,
                new BigDecimal("250.0000").compareTo(resultado.totalEmBrl())
        );

        verify(carteiraMapper).toResponse(carteira);
        verifyNoInteractions(awesomeApiClient);
    }

    @Test
    void deveRetornarListaVaziaETotalZeroSemCarteiras() {
        when(carteiraRepository.findAllByUsuarioEmail("teste@email.com"))
                .thenReturn(List.of());

        PatrimonioTotalResponse resultado =
                carteiraService.calcularTotalEmBrl("teste@email.com");

        assertNotNull(resultado);
        assertEquals("teste@email.com", resultado.usuarioEmail());
        assertTrue(resultado.carteiras().isEmpty());
        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(resultado.totalEmBrl())
        );

        verifyNoInteractions(carteiraMapper, awesomeApiClient);
    }

    @Test
    void deveLancarExcecaoQuandoCotacaoNaoForEncontrada() {
        Carteira carteiraDolar = mock(Carteira.class);

        when(carteiraRepository.findAllByUsuarioEmail("teste@email.com"))
                .thenReturn(List.of(carteiraDolar));

        when(carteiraDolar.getMoeda()).thenReturn(Moeda.USD);

        when(awesomeApiClient.buscarCotacao("USD-BRL"))
                .thenReturn(null);

        assertThrows(
                CotacaoIndisponivelException.class,
                () -> carteiraService.calcularTotalEmBrl("teste@email.com")
        );
    }

    @Test
    void deveLancarExcecaoQuandoBidForNulo() {
        Carteira carteiraDolar = mock(Carteira.class);
        AwesomeApiCotacaoResponse cotacao =
                mock(AwesomeApiCotacaoResponse.class);

        when(carteiraRepository.findAllByUsuarioEmail("teste@email.com"))
                .thenReturn(List.of(carteiraDolar));

        when(carteiraDolar.getMoeda()).thenReturn(Moeda.USD);

        when(awesomeApiClient.buscarCotacao("USD-BRL"))
                .thenReturn(Map.of("USDBRL", cotacao));

        when(cotacao.bid()).thenReturn(null);

        assertThrows(
                CotacaoIndisponivelException.class,
                () -> carteiraService.calcularTotalEmBrl("teste@email.com")
        );
    }

    @Test
    void deveConverterExcecaoDaApiEmCotacaoIndisponivel() {
        Carteira carteiraDolar = mock(Carteira.class);

        when(carteiraRepository.findAllByUsuarioEmail("teste@email.com"))
                .thenReturn(List.of(carteiraDolar));

        when(carteiraDolar.getMoeda()).thenReturn(Moeda.USD);

        when(awesomeApiClient.buscarCotacao("USD-BRL"))
                .thenThrow(new RuntimeException("Falha na API"));

        assertThrows(
                CotacaoIndisponivelException.class,
                () -> carteiraService.calcularTotalEmBrl("teste@email.com")
        );
    }
}