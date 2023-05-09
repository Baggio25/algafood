function consultarFormasPagamento() {
    $.ajax({
        url: "http://localhost:8080/formas-pagamento",
        type: "GET",
        
        success: function(response) {
            preencherTabela(response);
        }
    });
};

function cadastrarFormasPagamento() {
    var formaPagamentojSON = JSON.stringify({
        "descricao": $("#campo-descricao").val()
    })

    $.ajax({
        url: "http://localhost:8080/formas-pagamento",
        type: "POST",
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


function preencherTabela(formasPagamento) {
    $("#tabela tbody tr").remove();
  
    $.each(formasPagamento, function(i, formaPagamento) {
      var linha = $("<tr>");
  
      linha.append(
        $("<td>").text(formaPagamento.id),
        $("<td>").text(formaPagamento.descricao)
      );
  
      linha.appendTo("#tabela");
    });
  }



$("#botaoConsultar").click(consultarFormasPagamento);
$("#botaoCadastrar").click(cadastrarFormasPagamento);