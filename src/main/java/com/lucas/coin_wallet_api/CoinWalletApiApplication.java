package com.lucas.coin_wallet_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CoinWalletApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoinWalletApiApplication.class, args);
	}

}
