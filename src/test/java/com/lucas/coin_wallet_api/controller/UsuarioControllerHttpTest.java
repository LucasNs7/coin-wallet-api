package com.lucas.coin_wallet_api.controller;

import com.lucas.coin_wallet_api.config.SecurityConfig;
import com.lucas.coin_wallet_api.controller.dto.UsuarioRequest;
import com.lucas.coin_wallet_api.controller.dto.UsuarioResponse;
import com.lucas.coin_wallet_api.exception.ConflictException;
import com.lucas.coin_wallet_api.handler.GlobalExceptionHandler;
import com.lucas.coin_wallet_api.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@Import({
        GlobalExceptionHandler.class,
        SecurityConfig.class
})
class UsuarioControllerHttpTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    void deveCadastrarUsuarioComDadosValidos() throws Exception {
        UsuarioRequest request = new UsuarioRequest(
                "jonas",
                "jonas@email.com",
                "Senha@123"
        );

        UsuarioResponse response = new UsuarioResponse(
                "jonas",
                "jonas@email.com"
        );

        when(usuarioService.cadastrar(any(UsuarioRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("jonas"))
                .andExpect(jsonPath("$.email").value("jonas@email.com"));

        verify(usuarioService).cadastrar(any(UsuarioRequest.class));
    }

    @Test
    void deveRetornarBadRequestQuandoUsuarioForInvalido() throws Exception {
        UsuarioRequest request = new UsuarioRequest(
                "",
                "email-invalido",
                "123"
        );

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.fieldErrors.nome").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.senha").exists());

        verifyNoInteractions(usuarioService);
    }

    @Test
    void deveRetornarConflictQuandoEmailJaEstiverCadastrado()
            throws Exception {

        UsuarioRequest request = new UsuarioRequest(
                "jonas",
                "jonas@email.com",
                "Senha@123"
        );

        when(usuarioService.cadastrar(any(UsuarioRequest.class)))
                .thenThrow(new ConflictException("E-mail já cadastrado"));

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("E-mail já cadastrado"));
    }
}