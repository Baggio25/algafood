package com.algaworks.algafood.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/cozinhas")
public class CozinhaController {

	@GetMapping
	public String listar() {
		return "Listagem de Cozinhas";
	}
	
}
