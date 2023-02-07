package com.algaworks.algafood.api.exceptionhandler;

import lombok.Getter;

@Getter
public enum ErrorType {

	ENTIDADE_NAO_ENCONTRADA("/entidade-nao-econtrada", "Entidade não encontrada");
	
	private String title;
	private String uri;
	
	private ErrorType(String path, String title) {
		this.uri = "https://alfafood.com.br" + path;
		this.title = title;
	}
	
}
