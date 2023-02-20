package com.algaworks.algafood.api.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class RestauranteModel {

	private Long id;
	private String nome;
	private BigDecimal taxaFrete;
	private Boolean ativo;
	private Boolean aberto;

	private EnderecoModel endereco;
	private CozinhaModel cozinha; 
}
