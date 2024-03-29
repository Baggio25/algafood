package com.algaworks.algafood.api.exceptionhandler;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.algaworks.algafood.core.validation.exception.ValidacaoException;
import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.exception.NegocioException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.PropertyBindingException;

@ControllerAdvice
public class APIExeceptionHandler extends ResponseEntityExceptionHandler {

	public static final String MSG_ERRO_GENERICA_USUARIO_FINAL = "Ocorreu um erro interno inesperado no sistema. "
            + "Tente novamente e se o problema persistir, entre em contato "
            + "com o administrador do sistema.";

	@Autowired
	private MessageSource messageSource;	
	
	@ExceptionHandler({ ValidacaoException.class })
	public ResponseEntity<Object> handleValidacaoException(ValidacaoException ex, WebRequest request) {
	    return handleValidationInternal(ex, ex.getBindingResult(), new HttpHeaders(), 
	            HttpStatus.BAD_REQUEST, request);
	}  
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUncaught(Exception e, WebRequest request) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		APIErrorType apiErrorType = APIErrorType.ERRO_DE_SISTEMA;
		String detail = MSG_ERRO_GENERICA_USUARIO_FINAL;
		
		e.printStackTrace();
		
		APIError apiError = createErrorBuilder(status, apiErrorType, detail)
				.userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL)
				.build();
		
		return handleExceptionInternal(e, apiError, new HttpHeaders(), status, request);
	}
	
	@ExceptionHandler(EntidadeNaoEncontradaException.class)
	public ResponseEntity<?> handleEntidadeNaoEncontrada(EntidadeNaoEncontradaException ex, WebRequest request) {

		HttpStatus status = HttpStatus.NOT_FOUND;
		APIErrorType apiErrorType = APIErrorType.RECURSO_NAO_ENCONTRADO;
		String detail = ex.getMessage();

		APIError apiError = createErrorBuilder(status, apiErrorType, detail)
				.userMessage(detail)
				.build();

		return handleExceptionInternal(ex, apiError, new HttpHeaders(), status, request);
	}

	@ExceptionHandler(NegocioException.class)
	public ResponseEntity<?> handleNegocio(NegocioException ex, WebRequest request) {

		HttpStatus status = HttpStatus.BAD_REQUEST;
		APIErrorType apiErrorType = APIErrorType.ERRO_NEGOCIO;
		String detail = ex.getMessage();

		APIError apiError = createErrorBuilder(status, apiErrorType, detail)
				.userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL)
				.build();

		return handleExceptionInternal(ex, apiError, new HttpHeaders(), status, request);
	}

	@ExceptionHandler(EntidadeEmUsoException.class)
	public ResponseEntity<?> handleEntidadeEmUso(EntidadeEmUsoException ex, WebRequest request) {

		HttpStatus status = HttpStatus.CONFLICT;
		APIErrorType apiErrorType = APIErrorType.ENTIDADE_EM_USO;
		String detail = ex.getMessage();

		APIError apiError = createErrorBuilder(status, apiErrorType, detail)
				.userMessage(detail)
				.build();

		return handleExceptionInternal(ex, apiError, new HttpHeaders(), status, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.status(status).headers(headers).build();
	} 
	
	@Override
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {

		return handleValidationInternal(ex, ex.getBindingResult(), headers, status, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
	        HttpHeaders headers, HttpStatus status, WebRequest request) {
	    return handleValidationInternal(ex, ex.getBindingResult(), headers, status, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		if (ex instanceof MethodArgumentTypeMismatchException) {
			return handleMethodArgumentTypeMismatchException((MethodArgumentTypeMismatchException) ex, headers, status,
					request);
		}

		return super.handleTypeMismatch(ex, headers, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		
		APIErrorType apiErrorType = APIErrorType.RECURSO_NAO_ENCONTRADO;
		String detail = String.format("O recurso '%s', que você tentou acessar, é inexistente.", ex.getRequestURL());
		APIError apiError = createErrorBuilder(status, apiErrorType, detail)
				.userMessage(detail)
				.build();
		
		return handleExceptionInternal(ex, apiError, headers, status, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		Throwable rootCause = ExceptionUtils.getRootCause(ex);

		if (rootCause instanceof InvalidFormatException) {
			return handleInvalidFormat((InvalidFormatException) rootCause, headers, status, request);
		} else if (rootCause instanceof PropertyBindingException) {
			return handlePropertyBinding((PropertyBindingException) rootCause, headers, status, request);
		}

		APIErrorType apiErrorType = APIErrorType.MENSAGEM_INCOMPREENSIVEL;
		String detail = "O corpo da requisição está inválido. Verifique erro de sintaxe.";
		APIError apiError = createErrorBuilder(status, apiErrorType, detail)
				.userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL)
				.build();

		return handleExceptionInternal(ex, apiError, new HttpHeaders(), status, request);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		if (body == null) {
			body = APIError.builder()
					.timestamp(OffsetDateTime.now())
					.userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL)
					.title(status.getReasonPhrase())
					.status(status.value()).build();
		} else if (body instanceof String) {
			body = APIError.builder()
					.timestamp(OffsetDateTime.now())
					.userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL)
					.title((String) body)
					.status(status.value()).build();
		}

		return super.handleExceptionInternal(ex, body, headers, status, request);
	}
	
	private ResponseEntity<Object> handleValidationInternal(Exception ex, 
			BindingResult bindingResult, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		        
	    APIErrorType problemType = APIErrorType.DADOS_INVALIDOS;
	    String detail = "Um ou mais campos estão inválidos."
	    		+ " Faça o preenchimento correto e tente novamente.";
	    
	    List<APIError.Object> problemObjects = bindingResult.getAllErrors().stream()
	            .map(objectError -> {
	                String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());
	                
	                String name = objectError.getObjectName();
	                
	                if (objectError instanceof FieldError) {
	                    name = ((FieldError) objectError).getField();
	                }
	                
	                return APIError.Object.builder()
	                    .name(name)
	                    .userMessage(message)
	                    .build();
	            })
	            .collect(Collectors.toList());
	    
	    APIError apiError = createErrorBuilder(status, problemType, detail)
	        .userMessage(detail)
	        .objects(problemObjects)
	        .build();
	    
	    return handleExceptionInternal(ex, apiError, headers, status, request);
	}

	private ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		APIErrorType apiErrorType = APIErrorType.PARAMETRO_INVALIDO;
		String detail = String.format(
				"O parâmetro de URL '%s' recebeu o valor '%s', "
						+ "que é de um tipo inválido. Corrija e informe um valor compatível com o tipo %s.",
				ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());
		APIError apiError = createErrorBuilder(status, apiErrorType, detail)
				.userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL)
				.build();

		return handleExceptionInternal(ex, apiError, headers, status, request);
	}

	private ResponseEntity<Object> handleInvalidFormat(InvalidFormatException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		String path = joinPath(ex.getPath()); // Ex.: cozinha.id

		APIErrorType apiErrorType = APIErrorType.MENSAGEM_INCOMPREENSIVEL;
		String detail = String.format(
				"A propriedade '%s' recebeu o valor '%s', "
						+ "que é de um tipo inválido. Corrija e informe um valor compatível com o tipo '%s'.",
				path, ex.getValue(), ex.getTargetType().getSimpleName());
		APIError apiError = createErrorBuilder(status, apiErrorType, detail)
				.userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL)	
				.build();

		return handleExceptionInternal(ex, apiError, headers, status, request);
	}

	private ResponseEntity<Object> handlePropertyBinding(PropertyBindingException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		String path = joinPath(ex.getPath()); // Ex.: cozinha.id

		APIErrorType apiErrorType = APIErrorType.MENSAGEM_INCOMPREENSIVEL;
		String detail = String.format(
				"A propriedade '%s' não exi	ste. " + "Corrija ou remova essa propriedade e tente novamente", path);
		APIError apiError = createErrorBuilder(status, apiErrorType, detail)
				.userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL)
				.build();

		return handleExceptionInternal(ex, apiError, headers, status, request);
	}

	private String joinPath(List<Reference> references) {
		return references.stream().map(ref -> ref.getFieldName()).collect(Collectors.joining("."));
	}

	private APIError.APIErrorBuilder createErrorBuilder(HttpStatus status, APIErrorType apiErrorType, String detail) {
		return APIError.builder()
				.timestamp(OffsetDateTime.now())
				.status(status.value())
				.type(apiErrorType.getUri())
				.title(apiErrorType.getTitle())
				.detail(detail)
				.userMessage(MSG_ERRO_GENERICA_USUARIO_FINAL);					
	}
}
