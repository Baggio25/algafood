package com.algaworks.algafood.domain.exception;

public class RestauranteNaoEncontradoException extends EntidadeNaoEncontradaException {

	private static final long serialVersionUID = 1L;
	
	public RestauranteNaoEncontradoException(String msg) {
		super(msg);
	}

	public RestauranteNaoEncontradoException(Long id) {
		this(String.format(String.format("Não existe um cadastro de restaurante com o código %d", id)));
	}
}
