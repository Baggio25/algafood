package com.algaworks.algafood.api.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

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

import com.algaworks.algafood.api.assembler.RestauranteInputDisassembler;
import com.algaworks.algafood.api.assembler.RestauranteModelAssembler;
import com.algaworks.algafood.api.model.RestauranteModel;
import com.algaworks.algafood.api.model.input.RestauranteInput;
import com.algaworks.algafood.api.model.view.RestauranteView;
import com.algaworks.algafood.domain.exception.CidadeNaoEncontradaException;
import com.algaworks.algafood.domain.exception.CozinhaNaoEncontradaException;
import com.algaworks.algafood.domain.exception.NegocioException;
import com.algaworks.algafood.domain.exception.RestauranteNaoEncontradoException;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.RestauranteRepository;
import com.algaworks.algafood.domain.service.CadastroRestauranteService;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping(value = "/restaurantes")
public class RestauranteController {

	@Autowired
	private RestauranteRepository restauranteRepository;

	@Autowired
	private CadastroRestauranteService cadastroRestauranteService;
	
	@Autowired
	private RestauranteModelAssembler restauranteModelAssembler;
	
	@Autowired
	private RestauranteInputDisassembler restauranteInputDisassembler;
	
	@JsonView(RestauranteView.Resumo.class)
	@GetMapping
	public List<RestauranteModel> listar() {
		return restauranteModelAssembler.toCollectionModel(restauranteRepository.findAll());
	}
	
	@JsonView(RestauranteView.ApenasNome.class)
	@GetMapping(params = "projecao=apenas-nome")
	public List<RestauranteModel> listarApenasNome() {
		return listar();
	}
	
	@GetMapping(value = "/por-taxa-frete")
	public List<RestauranteModel> listarPorTaxaFrete(@PathParam("taxaInicial") BigDecimal taxaInicial,
			@PathParam("taxaFinal") BigDecimal taxaFinal) {

		return restauranteModelAssembler.toCollectionModel(restauranteRepository.findByTaxaFreteBetween(taxaInicial, taxaFinal));
	}

	@GetMapping(value = "/por-nome-e-cozinha")
	public List<RestauranteModel> listarPorTaxaFrete(@PathParam("nome") String nome,
			@PathParam("cozinhaId") Long cozinhaId) {

		return restauranteModelAssembler.toCollectionModel(restauranteRepository.buscarPorNome(nome, cozinhaId));
	}

	@GetMapping(value = "/por-nome-e-frete")
	public List<RestauranteModel> listarPorNomeFrete(@PathParam("nome") String nome,
			@PathParam("taxaInicial") BigDecimal taxaInicial, @PathParam("taxaFinal") BigDecimal taxaFinal) {

		return restauranteModelAssembler.toCollectionModel(restauranteRepository.find(nome, taxaInicial, taxaFinal));
	}

	@GetMapping(value = "/com-frete-gratis")
	public List<RestauranteModel> listarComFreteGratis(@PathParam("nome") String nome) {
		return restauranteModelAssembler.toCollectionModel(restauranteRepository.findComFreteGratis(nome));
	}

	@GetMapping(value = "/primeiro")
	public RestauranteModel restaurantePrimeiro() {
		return restauranteModelAssembler.toModel(cadastroRestauranteService.buscarPrimeiro());
	}

	@GetMapping(value = "/{id}")
	public RestauranteModel buscar(@PathVariable Long id) {
		Restaurante restaurante = cadastroRestauranteService.buscar(id);
		return restauranteModelAssembler.toModel(restaurante);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RestauranteModel salvar(@Valid @RequestBody RestauranteInput restauranteInput) {
		try {
			Restaurante restaurante = restauranteInputDisassembler.toDomainObject(restauranteInput);
			return restauranteModelAssembler.toModel(cadastroRestauranteService.salvar(restaurante));
		} catch (CozinhaNaoEncontradaException | CidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
	}

	@PutMapping(value = "/{id}")
	public RestauranteModel atualizar(@PathVariable Long id, 
			@Valid @RequestBody RestauranteInput restauranteInput) {
		try {
			Restaurante restauranteAtual = cadastroRestauranteService.buscar(id);
			restauranteInputDisassembler.copyToDomainObject(restauranteInput, restauranteAtual);

			return restauranteModelAssembler.toModel(cadastroRestauranteService.salvar(restauranteAtual));
		} catch (CozinhaNaoEncontradaException | CidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
	}
	
	@PutMapping(value = "/{id}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void ativar(@PathVariable Long id) {
		cadastroRestauranteService.ativar(id);
	}

	@DeleteMapping(value = "/{id}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void inativar(@PathVariable Long id) {
		cadastroRestauranteService.inativar(id);
	}
	
	@PutMapping(value = "/ativacoes")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void ativarMultiplos(@RequestBody List<Long> restauranteIds) {
		try {
			cadastroRestauranteService.ativar(restauranteIds);			
		} catch (RestauranteNaoEncontradoException e) {
			throw new NegocioException(e.getMessage(), e);
		}
	}
	
	@DeleteMapping(value = "/ativacoes")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void inativarMultiplos(@RequestBody List<Long> restauranteIds) {
		try {
			cadastroRestauranteService.inativar(restauranteIds);			
		} catch (RestauranteNaoEncontradoException e) {
			throw new NegocioException(e.getMessage(), e);
		}
	}
	
	@PutMapping(value = "/{id}/abertura")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void abrir(@PathVariable Long id) {
		cadastroRestauranteService.abrir(id);
	}
	
	@PutMapping(value = "/{id}/fechamento")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void fechar(@PathVariable Long id) {
		cadastroRestauranteService.fechar(id);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
		cadastroRestauranteService.excluir(id);
	}

}
