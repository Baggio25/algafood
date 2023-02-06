package com.algaworks.algafood.api.controller;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.websocket.server.PathParam;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.exception.NegocioException;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.RestauranteRepository;
import com.algaworks.algafood.domain.service.CadastroRestauranteService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/restaurantes")
public class RestauranteController {

	@Autowired
	private RestauranteRepository restauranteRepository;

	@Autowired
	private CadastroRestauranteService cadastroRestauranteService;

	@GetMapping
	public List<Restaurante> listar() {
		return restauranteRepository.findAll();
	}

	@GetMapping(value = "/por-taxa-frete")
	public List<Restaurante> listarPorTaxaFrete(@PathParam("taxaInicial") BigDecimal taxaInicial,
			@PathParam("taxaFinal") BigDecimal taxaFinal) {

		return restauranteRepository.findByTaxaFreteBetween(taxaInicial, taxaFinal);
	}

	@GetMapping(value = "/por-nome-e-cozinha")
	public List<Restaurante> listarPorTaxaFrete(@PathParam("nome") String nome,
			@PathParam("cozinhaId") Long cozinhaId) {

		return restauranteRepository.buscarPorNome(nome, cozinhaId);
	}

	@GetMapping(value = "/por-nome-e-frete")
	public List<Restaurante> listarPorNomeFrete(@PathParam("nome") String nome,
			@PathParam("taxaInicial") BigDecimal taxaInicial, @PathParam("taxaFinal") BigDecimal taxaFinal) {

		return restauranteRepository.find(nome, taxaInicial, taxaFinal);
	}

	@GetMapping(value = "/com-frete-gratis")
	public List<Restaurante> listarComFreteGratis(@PathParam("nome") String nome) {
		return restauranteRepository.findComFreteGratis(nome);
	}

	@GetMapping(value = "/primeiro")
	public Optional<Restaurante> restaurantePrimeiro() {
		return restauranteRepository.buscarPrimeiro();
	}

	@GetMapping(value = "/{id}")
	public Restaurante buscar(@PathVariable Long id) {
		return cadastroRestauranteService.buscar(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Restaurante salvar(@RequestBody Restaurante restaurante) {
		try {
			return cadastroRestauranteService.salvar(restaurante);
		} catch (EntidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
	}

	@PutMapping(value = "/{id}")
	public Restaurante atualizar(@PathVariable Long id, @RequestBody Restaurante restaurante) {
		Restaurante restauranteAtual = cadastroRestauranteService.buscar(id);

		BeanUtils.copyProperties(restaurante, restauranteAtual, "id", "formasPagamento", "endereco", "dataCadastro",
				"produtos");

		try {
			return cadastroRestauranteService.salvar(restauranteAtual);
		} catch (EntidadeNaoEncontradaException e) {
			throw new NegocioException(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
		cadastroRestauranteService.excluir(id);
	}

	@PatchMapping("/{id}")
	public Restaurante atualizarParcial(@PathVariable Long id, @RequestBody Map<String, Object> campos) {
		Restaurante restauranteAtual = cadastroRestauranteService.buscar(id);

		merge(campos, restauranteAtual);
		return atualizar(id, restauranteAtual);
	}

	private void merge(Map<String, Object> camposOrigem, Restaurante restauranteDestino) {
		ObjectMapper objectMapper = new ObjectMapper();
		Restaurante restauranteOrigem = objectMapper.convertValue(camposOrigem, Restaurante.class);

		camposOrigem.forEach((nomePropriedade, valorPropriedade) -> {
			Field field = ReflectionUtils.findField(Restaurante.class, nomePropriedade);
			field.setAccessible(true);

			Object novoValor = ReflectionUtils.getField(field, restauranteOrigem);

			ReflectionUtils.setField(field, restauranteDestino, novoValor);
		});
	}

}
