package com.algaworks.algafood.domain.exception;

public class GrupoNaoEncontradoException extends EntidadeNaoEncontradaException {

	private static final long serialVersionUID = 1L;
	
	public GrupoNaoEncontradoException(String msg) {
		super(msg);
	}

	public GrupoNaoEncontradoException(Long id) {
		this(String.format(String.format("Não existe um cadastro de grupo com o código %d", id)));
	}
}
