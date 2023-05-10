package com.algaworks.algafood.client;

import org.springframework.web.client.RestTemplate;

import com.algaworks.algafood.client.api.RestauranteClient;
import com.algaworks.algafood.client.api.exceptions.ClientAPIException;

public class ListagemRestaurantesMain {

	public static void main(String[] args) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			RestauranteClient restauranteClient = new RestauranteClient("http://localhost:8080", restTemplate);
			
			restauranteClient.listar().stream().forEach(
					(restaurante) -> System.out.println("----- Restaurante: " + restaurante.getNome() + ","
							+ " Taxa Frete: " + restaurante.getTaxaFrete() + ", "
							+ " Cozinha: " + restaurante.getCozinha().getNome())
			);
		} catch (ClientAPIException e) {
			if(e.getProblem() != null) {
				System.out.println(e.getProblem().getUserMessage());	
			} else {
				System.out.println("Erro desconhecido");
			}
			
		}
	}
	
}
