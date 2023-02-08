package com.algaworks.algafood.api.exceptionhandler;

import lombok.Getter;

@Getter
public enum ErrorType {

	ENTIDADE_NAO_ENCONTRADA("/entidade-nao-econtrada", "Entidade não encontrada"),
	ENTIDADE_EM_USO("/entidade-em-uso", "Entidade em uso"),
	ERRO_NEGOCIO("/erro-negocio", "Violação de regra de negócio"),
	MENSAGEM_INCOMPREENSIVEL("/mensagem-icompreensivel", "Mensagem incompreensível"),
	PARAMETRO_INVALIDO("/parametro-invalido", "Parâmetro inválido");
	
	private String title;
	private String uri;
	
	private ErrorType(String path, String title) {
		this.uri = "https://alfafood.com.br" + path;
		this.title = title;
	}
	
}
