function consultarRestaurantes() {
    $.ajax({
        url: "http://localhost:8080/restaurantes",
        type: "GET",
        
        success: function(response) {
            $("#conteudo").text(JSON.stringify(response));
        }
    });
};

function fecharRestaurante() {
    $.ajax({
        url: "http://localhost:8080/restaurantes/1/fechamento",
        type: "PUT",
        
        success: function(response) {
            alert("Restaurante foi fechado!")
        }
    });
}

$("#botao").click(consultarRestaurantes);
$("#botaoFechar").click(fecharRestaurante);