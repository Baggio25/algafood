package com.algaworks.algafood.domain.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.algaworks.algafood.domain.model.Restaurante;

@Repository
public interface RestauranteRepository extends 
	CustomJpaRepository<Restaurante, Long>, 
	RestauranteRepositoryQueries, 
	JpaSpecificationExecutor<Restaurante>{

	@Query("select distinct r from Restaurante r " +
			"join fetch r.cozinha " +
			"left join fetch r.formasPagamento")
	List<Restaurante> findAll();
	
	List<Restaurante> findByTaxaFreteBetween(BigDecimal taxaInicial, BigDecimal taxaFinal);
	
	// @Query("from Restaurante where nome like %:nome% and cozinha.id = :id")
	// Consulta criada em resources/META-INF/orm.xml como exemplo
	List<Restaurante> buscarPorNome(String nome, @Param("id") Long cozinhaId);
	
	List<Restaurante> find(String nome, BigDecimal taxaFreteInicial, BigDecimal taxaFreteFinal);
	
	
}