package com.algaworks.algafood.api.exceptionhandler;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

/**
 * Padrão: RFC 7807
 * 
 * @author rodri
 *
 */
@Getter 
@Builder
public class Erro {
	
	private LocalDateTime dataHora;
	private String mensagem;

}
