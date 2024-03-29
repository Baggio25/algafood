package com.algaworks.algafood.client.api;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.algaworks.algafood.client.api.exceptions.ClientAPIException;
import com.algaworks.algafood.client.model.RestauranteResumoModel;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RestauranteClient {
	
	private static final String RESOURCE_PATH = "/restaurantes";
	
	private String url;
	private RestTemplate restTemplate;

	public List<RestauranteResumoModel> listar() {
		
		try {
			URI resourceUri = URI.create(url + RESOURCE_PATH);
			RestauranteResumoModel[] restaurantes = restTemplate
					.getForObject(resourceUri, RestauranteResumoModel[].class);
			
			return Arrays.asList(restaurantes);
		}catch (RestClientResponseException e) {
			throw new ClientAPIException(e.getMessage(), e);
		}
	}

	
}
