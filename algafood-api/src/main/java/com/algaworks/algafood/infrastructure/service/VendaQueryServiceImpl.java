package com.algaworks.algafood.infrastructure.service;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.algaworks.algafood.domain.filter.VendaDiariaFilter;
import com.algaworks.algafood.domain.model.Pedido;
import com.algaworks.algafood.domain.model.dto.VendaDiaria;
import com.algaworks.algafood.domain.service.VendaQueryService;

@Repository
public class VendaQueryServiceImpl implements VendaQueryService{
	
	@Autowired
	private EntityManager manager;	
	
	/**
	 * Em SQL Ficaria:
	 * 
	 *  SELECT DATE(p.data_criacao) AS data_criacao,
	 *  	   COUNT(p.id) AS total_vendas,
	 *  	   SUM(p.valor_total) as total_faturado
	 *  FROM pedido p
	 *  GROUP BY DATE(p.data_criacao)
	 */
	@Override
	public List<VendaDiaria> consultarVendasDiarias(VendaDiariaFilter vendaDiariaFilter) {
		var builder = manager.getCriteriaBuilder();
		var query = builder.createQuery(VendaDiaria.class); //Define o tipo de retorno da query
		var root = query.from(Pedido.class);
		
		var functionDateDataCriacao = builder.function("date", Date.class, root.get("dataCriacao"));
		
		var selection = builder.construct(VendaDiaria.class, 
				functionDateDataCriacao,
				builder.count(root.get("id")),
				builder.sum(root.get("valorTotal")));
		
		query.select(selection);
		query.groupBy(functionDateDataCriacao);
		
		return manager.createQuery(query).getResultList();
	}

}
