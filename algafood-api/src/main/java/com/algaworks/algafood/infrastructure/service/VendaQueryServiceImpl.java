package com.algaworks.algafood.infrastructure.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.algaworks.algafood.domain.filter.VendaDiariaFilter;
import com.algaworks.algafood.domain.model.Pedido;
import com.algaworks.algafood.domain.model.StatusPedido;
import com.algaworks.algafood.domain.model.dto.VendaDiaria;
import com.algaworks.algafood.domain.service.VendaQueryService;

@Repository
public class VendaQueryServiceImpl implements VendaQueryService{
	
	@Autowired
	private EntityManager manager;	
	
	/**
	 * Com SQL Ficaria:
	 * 
	 *  SELECT DATE(p.data_criacao) AS data_criacao,
	 *  	   COUNT(p.id) AS total_vendas,
	 *  	   SUM(p.valor_total) as total_faturado
	 *  FROM pedido p
	 *  WHERE
	 *  	   restaurante_id = :restauranteId AND
	 *  	   data_criacao between :dataCriacaoInicio AND :dataCriacaoFim  
	 *  GROUP BY DATE(p.data_criacao)
	 */
	@Override
	public List<VendaDiaria> consultarVendasDiarias(VendaDiariaFilter vendaDiariaFilter) {
		var builder = manager.getCriteriaBuilder();
		var query = builder.createQuery(VendaDiaria.class); //Define o tipo de retorno da query
		var root = query.from(Pedido.class);
		var predicates = new ArrayList<Predicate>();
		
		var functionDateDataCriacao = builder.function("date", Date.class, root.get("dataCriacao"));
		
		var selection = builder.construct(VendaDiaria.class, 
				functionDateDataCriacao,
				builder.count(root.get("id")),
				builder.sum(root.get("valorTotal")));
		
		if(vendaDiariaFilter.getRestauranteId() != null) {
			predicates.add(builder.equal(root.get("restaurante"), vendaDiariaFilter.getRestauranteId()));
		}
		
		if(vendaDiariaFilter.getDataCriacaoInicio() != null) {
			predicates.add(builder.greaterThanOrEqualTo(root.get("dataCriacao"), vendaDiariaFilter.getDataCriacaoInicio()));
		}
		
		if(vendaDiariaFilter.getDataCriacaoFim() != null) {
			predicates.add(builder.lessThanOrEqualTo(root.get("dataCriacao"), vendaDiariaFilter.getDataCriacaoFim()));
		}
		
		predicates.add(root.get("status").in(StatusPedido.CONFIRMADO, StatusPedido.ENTREGUE));
		
		query.where(predicates.toArray(new Predicate[0]));
		query.select(selection);
		query.groupBy(functionDateDataCriacao);
		
		return manager.createQuery(query).getResultList();
	}

}
