package com.giantlink.project.mappers;

import java.util.List;
import java.util.Set;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.giantlink.project.entities.Commercial;
import com.giantlink.project.models.requests.CommercialRequest;
import com.giantlink.project.models.responses.CommercialResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CommercialMapper {

	CommercialMapper INSTANCE = Mappers.getMapper(CommercialMapper.class);

	List<CommercialResponse> mapCommercial(List<Commercial> Commercials);
	
	Set<CommercialResponse> mapCommercial(Set<Commercial> Commercials);
	
	Commercial requestToEntity(CommercialRequest commercialRequest);

	CommercialResponse entityToResponse(Commercial commercial);
}
