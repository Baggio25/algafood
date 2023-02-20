package com.algaworks.algafood.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.RestauranteNaoEncontradoException;
import com.algaworks.algafood.domain.model.Cidade;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.model.FormaPagamento;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.RestauranteRepository;

@Service
public class CadastroRestauranteService {
	
	private static final String MSG_RESTAURANTE_EM_USO = "Restaurante de código %d não pode ser removida, pois está em uso";

	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Autowired
	private CadastroCozinhaService cadastroCozinhaService;
	
	@Autowired
	private CadastroCidadeService cadastroCidadeService;

	@Autowired
	private CadastroFormaPagamentoService cadastroFormaPagamentoService;
	
	@Transactional
	public Restaurante salvar(Restaurante restaurante) {
		Long cozinhaId = restaurante.getCozinha().getId();
		Long cidadeId = restaurante.getEndereco().getCidade().getId();
		
		Cozinha cozinha = cadastroCozinhaService.buscar(cozinhaId);
		Cidade cidade = cadastroCidadeService.buscar(cidadeId);
				
		restaurante.setCozinha(cozinha);		
		restaurante.getEndereco().setCidade(cidade);
		
		return restauranteRepository.save(restaurante);
	}

	@Transactional
	public void excluir(Long id) {
		try {
			restauranteRepository.deleteById(id);
			restauranteRepository.flush(); //descarrega todas as operações, necessário pois foi utilizado @Transactional
		} catch (EmptyResultDataAccessException e) {
			throw new RestauranteNaoEncontradoException(id);
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format(MSG_RESTAURANTE_EM_USO, id)); 
		}		
	}

	@Transactional
	public void ativar(Long restauranteId) {
		Restaurante restauranteAtual = buscar(restauranteId);
		restauranteAtual.ativar();
	}

	@Transactional
	public void inativar(Long restauranteId) {
		Restaurante restauranteAtual = buscar(restauranteId);
		restauranteAtual.inativar();
	}
	
	public Restaurante buscar(Long id) {
		return restauranteRepository.findById(id)
				.orElseThrow(() -> new RestauranteNaoEncontradoException(id));
	}
	
	public Restaurante buscarPrimeiro() {
		return restauranteRepository.buscarPrimeiro()
				.orElseThrow(() -> new RestauranteNaoEncontradoException("Não há restaurante para exibir.")); 
	}

	@Transactional
	public void associarFormaPagamento(Long restauranteId, Long formaPagamentoId) {
		Restaurante restaurante = buscar(restauranteId);
		FormaPagamento formaPagamento = cadastroFormaPagamentoService.buscar(formaPagamentoId);
		
		restaurante.adicionarFormaPagamento(formaPagamento);
	}
	
	@Transactional
	public void desassociarFormaPagamento(Long restauranteId, Long formaPagamentoId) {
		Restaurante restaurante = buscar(restauranteId);
		FormaPagamento formaPagamento = cadastroFormaPagamentoService.buscar(formaPagamentoId);
		
		restaurante.removerFormaPagamento(formaPagamento);
	}

}
