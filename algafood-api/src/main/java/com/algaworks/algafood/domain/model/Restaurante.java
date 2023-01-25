package com.algaworks.algafood.domain.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "restaurante")
public class Restaurante {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nome;
	
	@Column(name = "taxa_frete")
	private BigDecimal taxaFrete;

	@ManyToOne
	@JoinColumn(name = "cozinha_id")
	private Cozinha cozinha;
	
	public Restaurante() {
	}
	
	public Restaurante(Long id, String nome, BigDecimal taxaFrete, Cozinha cozinha) {
		this.id = id;
		this.nome = nome;
		this.taxaFrete = taxaFrete;
		this.cozinha = cozinha;
	}

}
