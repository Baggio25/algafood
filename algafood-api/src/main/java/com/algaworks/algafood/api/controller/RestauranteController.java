package com.algaworks.algafood.api.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.springframework.beans.BeanUtils;
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

import com.algaworks.algafood.api.assembler.RestauranteModelAssembler;
import com.algaworks.algafood.api.model.RestauranteModel;
import com.algaworks.algafood.api.model.input.RestauranteInput;
import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.exception.NegocioException;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.RestauranteRepository;
import com.algaworks.algafood.domain.service.CadastroRestauranteService;

@RestController
@RequestMapping(value = "/restaurantes")
public class RestauranteController {

	@Autowired
	private RestauranteRepository restauranteRepository;

	@Autowired
	private CadastroRestauranteService cadastroRestauranteService;
	
	@Autowired
	private RestauranteModelAssembler restauranteModelAssembler;
	
	@GetMapping
	public List<RestauranteModel> listar() {
		return restauranteModelAssembler.toCollectionModel(restauranteRepository.findAll());
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
			Restaurante restaurante = toDomainObject(restauranteInput);
			return restauranteModelAssembler.toModel(cadastroRestauranteService.salvar(restaurante));
		} catch (EntidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
	}

	@PutMapping(value = "/{id}")
	public RestauranteModel atualizar(@PathVariable Long id, 
			@Valid @RequestBody RestauranteInput restauranteInput) {
		Restaurante restaurante = toDomainObject(restauranteInput);
		Restaurante restauranteAtual = cadastroRestauranteService.buscar(id);

		BeanUtils.copyProperties(restaurante, restauranteAtual, "id", "formasPagamento", "endereco", "dataCadastro",
				"produtos");

		try {
			return restauranteModelAssembler.toModel(cadastroRestauranteService.salvar(restauranteAtual));
		} catch (EntidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
		cadastroRestauranteService.excluir(id);
	}

	private Restaurante toDomainObject(RestauranteInput restauranteInput) {
		Restaurante restaurante = new Restaurante();
		restaurante.setNome(restauranteInput.getNome());
		restaurante.setTaxaFrete(restauranteInput.getTaxaFrete());
		
		Cozinha cozinha = new Cozinha();
		cozinha.setId(restauranteInput.getCozinha().getId());
		
		restaurante.setCozinha(cozinha);
		return restaurante;
	}

}
