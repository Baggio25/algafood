package com.algaworks.algafood.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.api.assembler.CozinhaInputDisassembler;
import com.algaworks.algafood.api.assembler.CozinhaModelAssembler;
import com.algaworks.algafood.api.model.CozinhaModel;
import com.algaworks.algafood.api.model.input.CozinhaInput;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.repository.CozinhaRepository;
import com.algaworks.algafood.domain.service.CadastroCozinhaService;

@RestController
@RequestMapping(value = "/cozinhas")
public class CozinhaController {

	@Autowired
	private CozinhaRepository cozinhaRepository;

	@Autowired
	private CadastroCozinhaService cadastroCozinhaService;

	@Autowired
	private CozinhaModelAssembler cozinhaModelAssembler;

	@Autowired
	private CozinhaInputDisassembler cozinhaInputDisassembler;     
	
	@GetMapping
	public List<CozinhaModel> listar() {
		return cozinhaModelAssembler.toCollectionModel(cozinhaRepository.findAll());
	}

	@GetMapping("/por-nome")
	public List<CozinhaModel> listarPorNome(@RequestParam("nome") String nome) {
		return cozinhaModelAssembler.toCollectionModel(cozinhaRepository.findByNomeContaining(nome));
	}

	@GetMapping("/{id}")
	public CozinhaModel buscar(@PathVariable Long id) {
		return cozinhaModelAssembler.toModel(cadastroCozinhaService.buscar(id));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CozinhaModel salvar(@Valid @RequestBody CozinhaInput cozinhaInput) {
		Cozinha cozinha = cozinhaInputDisassembler.toDomainObject(cozinhaInput);
		cozinha = cadastroCozinhaService.salvar(cozinha);
		
		return cozinhaModelAssembler.toModel(cozinha); 
	}

	@PutMapping("/{id}")
	public CozinhaModel atualizar(@PathVariable Long id, @Valid @RequestBody CozinhaInput cozinhaInput) {
		Cozinha cozinhaAtual = cadastroCozinhaService.buscar(id);

		cozinhaInputDisassembler.copyToDomainObject(cozinhaInput, cozinhaAtual);
		cozinhaAtual = cadastroCozinhaService.salvar(cozinhaAtual);
		
		return cozinhaModelAssembler.toModel(cozinhaAtual);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
		cadastroCozinhaService.excluir(id);
	}

}
