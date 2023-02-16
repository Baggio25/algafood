package com.algaworks.algafood.core.modelmapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.algaworks.algafood.api.model.EnderecoModel;
import com.algaworks.algafood.domain.model.Endereco;

@Configuration
public class ModelMapperConfig {

	@Bean
	public ModelMapper modelMapper() {
		var modelMapper = new ModelMapper(); 		
		var enderecoToEnderecoModel = modelMapper.createTypeMap(Endereco.class, EnderecoModel.class);
		
		enderecoToEnderecoModel.<String>addMapping(
				enderecoSrc -> enderecoSrc.getCidade().getEstado().getNome(), 
				(enderecoModelDest, valor) -> enderecoModelDest.getCidade().setEstado(valor));
		
		return modelMapper;
	}
	
}
