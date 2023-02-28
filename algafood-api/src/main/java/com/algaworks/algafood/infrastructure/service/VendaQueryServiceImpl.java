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
	 *  SELECT DATE(CONVERT_TZ(p.data_criacao, '+00:00', '-03:00')) AS data_criacao,
	 *  	   COUNT(p.id) AS total_vendas,
	 *  	   SUM(p.valor_total) as total_faturado
	 *  FROM pedido p
	 *  WHERE
	 *  	   p.restaurante_id = :restauranteId AND
	 *  	   p.data_criacao between :dataCriacaoInicio AND :dataCriacaoFim AND
	 *  	   p.status IN ('CRIADO', 'ENTREGUE')  
	 *  GROUP BY DATE(CONVERT_TZ(p.data_criacao, '+00:00', '-03:00'))
	 */
	
	@Override
	public List<VendaDiaria> consultarVendasDiarias(VendaDiariaFilter vendaDiariaFilter, String timeOffset) {
		var builder = manager.getCriteriaBuilder();
		var query = builder.createQuery(VendaDiaria.class); //Define o tipo de retorno da query
		var root = query.from(Pedido.class);
		var predicates = new ArrayList<Predicate>();
	
		var functionConvertTzDataCriacao = builder.function(
				"convert_tz", 
				Date.class, 
				root.get("dataCriacao"),
				builder.literal("+00:00"),
				builder.literal(timeOffset));
		
		var functionDateDataCriacao = builder.function(
				"date", 
				Date.class, 
				functionConvertTzDataCriacao);
		
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
