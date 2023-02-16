package com.algaworks.algafood.domain.exception;

public class FormaPagamentoNaoEncontradaException extends EntidadeNaoEncontradaException {

    public FormaPagamentoNaoEncontradaException(String msg) {
        super(msg);
    }

    public FormaPagamentoNaoEncontradaException(Long id) {
        this(String.format(String.format("Não existe um cadastro de forma de pagamento com o código %d", id)));
    }
}
