function consultarFormasPagamento() {
    $.ajax({
        url: "http://localhost:8080/formas-pagamento",
        type: "get",
        
        success: function(response) {
            preencherTabela(response);
        }
    });
};

function cadastrarFormaPagamento() {
    var formaPagamentojSON = JSON.stringify({
        "descricao": $("#campo-descricao").val()
    })

    $.ajax({
        url: "http://localhost:8080/formas-pagamento",
        type: "post",
        data: formaPagamentojSON,
        contentType: "application/json",

        success: function(response) {
            alert("Forma de Pagamento " + response.id +" - " + response.descricao + " adicionada!");
            consultarFormasPagamento();
            $("#campo-descricao").val("");
        },

        error: function(error) {
            if(error.status === 400) {
                var problem = JSON.parse(error.responseText);
                alert(problem.userMessage);
            }else {
                alert("Erro ao cadastrar forma de pagamento!");
            }
        }
    });
};

function excluirFormaPagamento(formaPagamento) {
    $.ajax({
        url: "http://localhost:8080/formas-pagamento/" + formaPagamento.id,
        type: "delete",

        success: function(response) {
            consultarFormasPagamento();
            alert("Forma de pagamento removida!");
        },

        error: function(error) {
            if (error.status >= 400 && error.status <= 499) {
                var problem = JSON.parse(error.responseText);
                alert(problem.userMessage);
            } else {
                alert("Erro ao remover forma de pagamento!");
            }
        }

    })
}

function preencherTabela(formasPagamento) {
    $("#tabela tbody tr").remove();

    $.each(formasPagamento, function(i, formaPagamento) {
        var linha = $("<tr>");

        var linkAcao = $("<a href='#'>")
            .text("Excluir")
            .click(function(event) {
                event.preventDefault();
                excluirFormaPagamento(formaPagamento);
            });

        linha.append(
            $("<td>").text(formaPagamento.id),
            $("<td>").text(formaPagamento.descricao),
            $("<td>").append(linkAcao)
        );

        linha.appendTo("#tabela");
    });
}
  



$("#botaoConsultar").click(consultarFormasPagamento);
$("#botaoCadastrar").click(cadastrarFormaPagamento);