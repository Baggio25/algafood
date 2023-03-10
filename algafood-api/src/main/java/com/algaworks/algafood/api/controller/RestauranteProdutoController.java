package com.algaworks.algafood.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.api.assembler.ProdutoInputDisassembler;
import com.algaworks.algafood.api.assembler.ProdutoModelAssembler;
import com.algaworks.algafood.api.model.ProdutoModel;
import com.algaworks.algafood.api.model.input.ProdutoInput;
import com.algaworks.algafood.domain.model.Produto;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.ProdutoRepository;
import com.algaworks.algafood.domain.service.CadastroProdutoService;
import com.algaworks.algafood.domain.service.CadastroRestauranteService;

@RestController
@RequestMapping(value = "/restaurantes/{restauranteId}/produtos")
public class RestauranteProdutoController {

	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private CadastroProdutoService cadastroProdutoService;

	@Autowired
	private CadastroRestauranteService cadastroRestauranteService;
	
	@Autowired
	private ProdutoModelAssembler produtoModelAssembler;
	
	@Autowired
	private ProdutoInputDisassembler produtoInputDisassembler;
	
	@GetMapping
	public List<ProdutoModel> listar(@PathVariable Long restauranteId,
			@RequestParam(required = false) Boolean incluirInativos) {
		Restaurante restaurante = cadastroRestauranteService.buscar(restauranteId);
		List<Produto> produtos = null;
		
		if(incluirInativos) {
			produtos = produtoRepository.findAllByRestaurante(restaurante);
		} else {			
			produtos = produtoRepository.findAtivosByRestaurante(restaurante);
		}
		
		return produtoModelAssembler.toCollectionModel(produtos);
	}
	
	@GetMapping(value = "/{produtoId}")
	public ProdutoModel buscar(@PathVariable Long restauranteId) {
		Produto produto = cadastroProdutoService.buscar(restauranteId, restauranteId);		
		
		return produtoModelAssembler.toModel(produto);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProdutoModel salvar(@PathVariable Long restauranteId, 
			@Valid @RequestBody ProdutoInput produtoInput) {
		Restaurante restaurante = cadastroRestauranteService.buscar(restauranteId);
		
		Produto produto = produtoInputDisassembler.toDomainObject(produtoInput);
		produto.setRestaurante(restaurante);		
		produto = cadastroProdutoService.salvar(produto);
		
		return produtoModelAssembler.toModel(produto);
	}
	
	@PutMapping(value = "/{produtoId}")
	public ProdutoModel atualizar(@PathVariable Long restauranteId,  @PathVariable Long produtoId, 
			@Valid @RequestBody ProdutoInput produtoInput) {
		
		Produto produtoAtual = cadastroProdutoService.buscar(restauranteId, produtoId);
		produtoInputDisassembler.copyToDomainObject(produtoInput, produtoAtual);
		produtoAtual = cadastroProdutoService.salvar(produtoAtual);
	
		return produtoModelAssembler.toModel(produtoAtual);
	}
	
	
}
