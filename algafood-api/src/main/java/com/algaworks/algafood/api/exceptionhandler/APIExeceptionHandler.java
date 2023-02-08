package com.algaworks.algafood.api.exceptionhandler;

import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.exception.NegocioException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@ControllerAdvice
public class APIExeceptionHandler extends ResponseEntityExceptionHandler {
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		Throwable rootCause = ExceptionUtils.getRootCause(ex);
		
		if(rootCause instanceof InvalidFormatException) {
			return handleInvalidFormatException((InvalidFormatException) rootCause, headers, status, request);
		}
		
		ErrorType errorType = ErrorType.MENSAGEM_INCOMPREENSIVEL;
		String detail = "O corpo da requisição está inválido. Verifique erro de sintaxe.";

		Error error = createErrorBuilder(status, errorType, detail).build();
		
		return handleExceptionInternal(ex, error, new HttpHeaders(), status, request);
	}


	@ExceptionHandler(EntidadeNaoEncontradaException.class)
	public ResponseEntity<?> handleEntidadeNaoEncontradaException(EntidadeNaoEncontradaException ex,
			WebRequest request) {

		HttpStatus status = HttpStatus.NOT_FOUND;
		ErrorType errorType = ErrorType.ENTIDADE_NAO_ENCONTRADA;
		String detail = ex.getMessage();

		Error error = createErrorBuilder(status, errorType, detail).build();

		return handleExceptionInternal(ex, error, new HttpHeaders(), status, request);
	}

	@ExceptionHandler(NegocioException.class)
	public ResponseEntity<?> handleNegocioException(NegocioException ex, WebRequest request) {

		HttpStatus status = HttpStatus.BAD_REQUEST;
		ErrorType errorType = ErrorType.ERRO_NEGOCIO;
		String detail = ex.getMessage();

		Error error = createErrorBuilder(status, errorType, detail).build();

		return handleExceptionInternal(ex, error, new HttpHeaders(), status, request);
	}

	@ExceptionHandler(EntidadeEmUsoException.class)
	public ResponseEntity<?> handleEntidadeEmUsoException(EntidadeEmUsoException ex, WebRequest request) {

		HttpStatus status = HttpStatus.CONFLICT;
		ErrorType errorType = ErrorType.ENTIDADE_EM_USO;
		String detail = ex.getMessage();

		Error error = createErrorBuilder(status, errorType, detail).build();

		return handleExceptionInternal(ex, error, new HttpHeaders(), status, request);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		if (body == null) {
			body = Error.builder().title(status.getReasonPhrase()).status(status.value()).build();
		} else if (body instanceof String) {
			body = Error.builder().title((String) body).status(status.value()).build();
		}

		return super.handleExceptionInternal(ex, body, headers, status, request);
	}

	private Error.ErrorBuilder createErrorBuilder(HttpStatus status, ErrorType errorType, String detail) {
		return Error.builder().status(status.value()).type(errorType.getUri()).title(errorType.getTitle())
				.detail(detail);
	}

	private ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		String path = ex.getPath().stream()
				.map(ref -> ref.getFieldName())
				.collect(Collectors.joining(".")); //Ex.: cozinha.id
		
		ErrorType errorType = ErrorType.MENSAGEM_INCOMPREENSIVEL;
		String detail = String.format("A propriedade '%s' recebeu o valor '%s', "
				+ "que é de um tipo inválido. Corrija e informe um valor compatível com o tipo '%s'.", 
				path, ex.getValue(), ex.getTargetType().getSimpleName());
		Error error = createErrorBuilder(status, errorType, detail).build();;
		
		return handleExceptionInternal(ex, error, headers, status, request);
	}
}
