package com.algaworks.algafood.domain.exception;

public class CozinhaNaoEncontradaException extends EntidadeNaoEncontradaException {

	private static final long serialVersionUID = 1L;
	
	public CozinhaNaoEncontradaException(String msg) {
		super(msg);
	}

	public CozinhaNaoEncontradaException(Long id) {
		this(String.format(String.format("Não existe um cadastro de cozinha com o código %d", id)));
	}
}
