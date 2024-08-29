package com.giantlink.project.mappers;

import java.util.List;
import java.util.Set;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.giantlink.project.entities.Option;
import com.giantlink.project.models.requests.OptionRequest;
import com.giantlink.project.models.responses.OptionResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OptionMapper {

	OptionMapper INSTANCE = Mappers.getMapper(OptionMapper.class);

	List<OptionResponse> mapOption(List<Option> options);
	
	Set<OptionResponse> mapOption(Set<Option> options);
	
	Option requestToEntity(OptionRequest optionRequest);

	OptionResponse entityToResponse(Option option);
}
