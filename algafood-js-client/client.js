function consultarRestaurantes() {
    $.ajax({
        url: "http://localhost:8080/restaurantes",
        type: "GET",
        
        success: function(response) {
            $("#conteudo").text(JSON.stringify(response));
        }
    });
}

$("#botao").click(consultarRestaurantes);