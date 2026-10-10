package com.lucas.coin_wallet_api.controller;

import com.lucas.coin_wallet_api.config.SecurityConfig;
import com.lucas.coin_wallet_api.controller.dto.CarteiraRequest;
import com.lucas.coin_wallet_api.exception.ResourceNotFoundException;
import com.lucas.coin_wallet_api.handler.GlobalExceptionHandler;
import com.lucas.coin_wallet_api.service.CarteiraService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarteiraController.class)
@Import({
        GlobalExceptionHandler.class,
        SecurityConfig.class
})
class CarteiraControllerHttpTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CarteiraService carteiraService;

    @Test
    void deveRetornarBadRequestQuandoCarteiraForInvalida()
            throws Exception {

        CarteiraRequest request = new CarteiraRequest(
                "email-invalido",
                null,
                BigDecimal.ZERO
        );

        mockMvc.perform(post("/carteiras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.usuarioEmail").exists())
                .andExpect(jsonPath("$.fieldErrors.moeda").exists())
                .andExpect(jsonPath("$.fieldErrors.quantidade").exists());

        verifyNoInteractions(carteiraService);
    }

    @Test
    void deveRetornarNotFoundQuandoUsuarioNaoExistir()
            throws Exception {

        when(carteiraService.calcularTotalEmBrl("ausente@email.com"))
                .thenThrow(new ResourceNotFoundException(
                        "Usuário não encontrado"
                ));

        mockMvc.perform(get(
                        "/carteiras/total-brl/ausente@email.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Usuário não encontrado"));
    }
}