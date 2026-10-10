package com.lucas.coin_wallet_api.client;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class AwesomeApiClientTest {

    @Test
    void deveEstarConfiguradoComoClienteFeign() {
        FeignClient annotation =
                AwesomeApiClient.class.getAnnotation(FeignClient.class);

        assertNotNull(annotation);
        assertEquals("awesomeApiClient", annotation.name());
        assertEquals(
                "${awesomeapi.url:https://economia.awesomeapi.com.br}",
                annotation.url()
        );
    }

    @Test
    void deveConsultarCotacaoPeloEndpointEsperado() throws Exception {
        Method method = AwesomeApiClient.class.getMethod(
                "buscarCotacao",
                String.class
        );

        GetMapping mapping = method.getAnnotation(GetMapping.class);

        assertNotNull(mapping);
        assertArrayEquals(new String[]{"/last/{par}"}, mapping.value());

        PathVariable pathVariable =
                method.getParameters()[0].getAnnotation(PathVariable.class);

        assertNotNull(pathVariable);
        assertEquals("par", pathVariable.value());
    }
}