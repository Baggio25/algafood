package com.algaworks.algafood.domain.exception;

public class CidadeNaoEncontradaException extends EntidadeNaoEncontradaException {

	private static final long serialVersionUID = 1L;
	
	public CidadeNaoEncontradaException(String msg) {
		super(msg);
	}

	public CidadeNaoEncontradaException(Long id) {
		this(String.format(String.format("Não existe um cadastro de cidade com o código %d", id)));
	}
}
