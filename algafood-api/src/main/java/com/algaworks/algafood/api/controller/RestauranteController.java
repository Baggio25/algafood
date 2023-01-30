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
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.RestauranteRepository;
import com.algaworks.algafood.domain.service.CadastroRestauranteService;
import com.algaworks.algafood.infrastructure.repository.specification.RestauranteSpecs;
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
			@PathParam("taxaInicial") BigDecimal taxaInicial,
			@PathParam("taxaFinal") BigDecimal taxaFinal) {
		
		return restauranteRepository.find(nome, taxaInicial, taxaFinal);
	}
	
	@GetMapping(value = "/com-frete-gratis")
	public List<Restaurante> listarComFreteGratis(@PathParam("nome") String nome) {
		return restauranteRepository.findAll(RestauranteSpecs.comFreteGratis()
				.and(RestauranteSpecs.comNomeSemelhante(nome)));
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<Restaurante> buscar(@PathVariable Long id) {
		Optional<Restaurante> restaurante = restauranteRepository.findById(id);

		if (restaurante.isPresent()) {
			return ResponseEntity.ok(restaurante.get());
		}

		return ResponseEntity.notFound().build();
	}

	@PostMapping
	public ResponseEntity<?> salvar(@RequestBody Restaurante restaurante) {
		try {
			restaurante = cadastroRestauranteService.salvar(restaurante);
			return ResponseEntity.status(HttpStatus.CREATED).body(restaurante);
		} catch (EntidadeNaoEncontradaException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Restaurante restaurante) {
		Optional<Restaurante> restauranteAtual = restauranteRepository.findById(id);
		
		if (restauranteAtual.isPresent()) {
			BeanUtils.copyProperties(restaurante, restauranteAtual.get(), "id");
			
			Restaurante restauranteSalvo = cadastroRestauranteService.salvar(restauranteAtual.get());
			return ResponseEntity.ok(restauranteSalvo);
		}
		
		return ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Restaurante> remover(@PathVariable Long id) {
		try {
			cadastroRestauranteService.excluir(id);	
			return ResponseEntity.noContent().build();
			
		} catch (EntidadeNaoEncontradaException e) {
			return ResponseEntity.notFound().build();
			
		} catch (EntidadeEmUsoException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}
	
	
	@PatchMapping("/{id}")
	public ResponseEntity<?> atualizarParcial(@PathVariable Long id, 
			@RequestBody Map<String, Object> campos) {
		Optional<Restaurante> restauranteAtual = restauranteRepository.findById(id);
		
		if (restauranteAtual.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		
		merge(campos, restauranteAtual.get());		
		return atualizar(id, restauranteAtual.get());
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
