package com.algaworks.algafood.domain.exception;

public class FotoNaoEncontradaException extends EntidadeNaoEncontradaException {

	private static final long serialVersionUID = 1L;
	
	public FotoNaoEncontradaException(String msg) {
		super(msg);
	}

	public FotoNaoEncontradaException(Long restauranteId, Long produtoId) {
		this(String.format(String.format("Não existe um cadastro de foto do produto com o código %d para o "
				+ "restaurante de código %d", produtoId, restauranteId)));
	}
}
