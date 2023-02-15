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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.api.assembler.CidadeInputDisassembler;
import com.algaworks.algafood.api.assembler.CidadeModelAssembler;
import com.algaworks.algafood.api.model.CidadeModel;
import com.algaworks.algafood.api.model.input.CidadeInput;
import com.algaworks.algafood.domain.exception.EstadoNaoEncontradoException;
import com.algaworks.algafood.domain.exception.NegocioException;
import com.algaworks.algafood.domain.model.Cidade;
import com.algaworks.algafood.domain.repository.CidadeRepository;
import com.algaworks.algafood.domain.service.CadastroCidadeService;

@RestController
@RequestMapping(value = "/cidades")
public class CidadeController {

	@Autowired
	private CidadeRepository cidadeRepository;

	@Autowired
	private CadastroCidadeService cadastroCidadeService;
	
	@Autowired
	private CidadeModelAssembler cidadeModelAssembler;
	
	@Autowired
	private CidadeInputDisassembler cidadeInputDisassembler;

	@GetMapping
	public List<CidadeModel> listar() {
		return cidadeModelAssembler.toCollectionModel(cidadeRepository.findAll());
	}

	@GetMapping(value = "/{id}")
	public CidadeModel buscar(@PathVariable Long id) {
		return cidadeModelAssembler.toModel(cadastroCidadeService.buscar(id));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CidadeModel salvar(@Valid @RequestBody CidadeInput cidadeInput) {
		Cidade cidade = cidadeInputDisassembler.toDomainObject(cidadeInput);
		cidade = cadastroCidadeService.salvar(cidade);

		return cidadeModelAssembler.toModel(cidade);
	}

	@PutMapping(value = "/{id}")
	public CidadeModel atualizar(@Valid @PathVariable Long id, @RequestBody CidadeInput cidadeInput) {
		try {
			Cidade cidadeAtual = cadastroCidadeService.buscar(id);
			
			cidadeInputDisassembler.copyToDomainObject(cidadeInput, cidadeAtual);
			
			return cidadeModelAssembler.toModel(cidadeAtual);			
		} catch (EstadoNaoEncontradoException e) {
			throw new NegocioException(e.getMessage(), e);
		}
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
		cadastroCidadeService.excluir(id);
	}
}