package com.lucas.coin_wallet_api.controller;

import com.lucas.coin_wallet_api.controller.dto.CarteiraRequest;
import com.lucas.coin_wallet_api.controller.dto.CarteiraResponse;
import com.lucas.coin_wallet_api.controller.dto.PatrimonioTotalResponse;
import com.lucas.coin_wallet_api.service.CarteiraService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarteiraControllerTest {

    @Mock
    private CarteiraService carteiraService;

    @InjectMocks
    private CarteiraController carteiraController;

    @Test
    void deveCriarOuAtualizarCarteira() {
        CarteiraRequest request = mock(CarteiraRequest.class);
        CarteiraResponse response = mock(CarteiraResponse.class);

        when(carteiraService.criarOuAtualizar(request)).thenReturn(response);

        ResponseEntity<CarteiraResponse> resultado =
                carteiraController.criarOuAtualizar(request);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertSame(response, resultado.getBody());

        verify(carteiraService).criarOuAtualizar(request);
    }

    @Test
    void deveCalcularPatrimonioTotalEmBrl() {
        String email = "teste@email.com";
        PatrimonioTotalResponse response =
                mock(PatrimonioTotalResponse.class);

        when(carteiraService.calcularTotalEmBrl(email)).thenReturn(response);

        ResponseEntity<PatrimonioTotalResponse> resultado =
                carteiraController.calcularTotalEmBrl(email);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertSame(response, resultado.getBody());

        verify(carteiraService).calcularTotalEmBrl(email);
    }
}