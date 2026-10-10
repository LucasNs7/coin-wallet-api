package com.lucas.coin_wallet_api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(
        name = "awesomeApiClient",
        url = "${awesomeapi.url:https://economia.awesomeapi.com.br}"
)
public interface AwesomeApiClient {

    @GetMapping("/last/{par}")
    Map<String, AwesomeApiCotacaoResponse> buscarCotacao(@PathVariable("par") String par);
}