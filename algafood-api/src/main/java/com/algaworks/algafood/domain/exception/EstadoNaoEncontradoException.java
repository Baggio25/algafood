package com.algaworks.algafood.domain.exception;

public class EstadoNaoEncontradoException extends EntidadeNaoEncontradaException {

	private static final long serialVersionUID = 1L;
	
	public EstadoNaoEncontradoException(String msg) {
		super(msg);
	}

	public EstadoNaoEncontradoException(Long estadoId) {
		this(String.format(String.format("Não existe um cadastro de estado com o código %d", estadoId)));
	}
}
