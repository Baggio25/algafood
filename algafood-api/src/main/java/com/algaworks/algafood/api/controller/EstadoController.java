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

import com.algaworks.algafood.api.assembler.EstadoInputDisassembler;
import com.algaworks.algafood.api.assembler.EstadoModelAssembler;
import com.algaworks.algafood.api.model.EstadoModel;
import com.algaworks.algafood.api.model.input.EstadoInput;
import com.algaworks.algafood.domain.model.Estado;
import com.algaworks.algafood.domain.repository.EstadoRepository;
import com.algaworks.algafood.domain.service.CadastroEstadoService;

@RestController
@RequestMapping(value = "/estados")
public class EstadoController {

	@Autowired
	private EstadoRepository estadoRepository;

	@Autowired
	private CadastroEstadoService cadastroEstadoService;
	
	@Autowired
	private EstadoModelAssembler estadoModelAssembler;
	
	@Autowired
	private EstadoInputDisassembler estadoInputDisassembler;

	@GetMapping
	public List<EstadoModel> listar() {
		return estadoModelAssembler.toCollectionModel(estadoRepository.findAll());
	}

	@GetMapping(value = "/{id}")
	public EstadoModel buscar(@PathVariable Long id) {
		return estadoModelAssembler.toModel(cadastroEstadoService.buscar(id));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EstadoModel salvar(@Valid @RequestBody EstadoInput estadoInput) {
		Estado estado = estadoInputDisassembler.toDomainOject(estadoInput);
		estado = cadastroEstadoService.salvar(estado);

		return estadoModelAssembler.toModel(estado); 
	}

	@PutMapping(value = "/{id}")
	public EstadoModel atualizar( @PathVariable Long id, @Valid @RequestBody EstadoInput estadoInput) {
		Estado estadoAtual = cadastroEstadoService.buscar(id);
		
		estadoInputDisassembler.copyToDomainObject(estadoInput, estadoAtual);
		estadoAtual = cadastroEstadoService.salvar(estadoAtual);

		return estadoModelAssembler.toModel(estadoAtual);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
		cadastroEstadoService.excluir(id);
	}

}
