package com.giantlink.project.mappers;

import java.util.List;
import java.util.Set;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.giantlink.project.entities.Lead;
import com.giantlink.project.models.requests.LeadRequest;
import com.giantlink.project.models.responses.LeadResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface LeadMapper {

	LeadMapper INSTANCE = Mappers.getMapper(LeadMapper.class);

	List<LeadResponse> mapLead(List<Lead> leads);
	
	Set<LeadResponse> mapLead(Set<Lead> leads);
	
	Lead requestToEntity(LeadRequest leadRequest);

	LeadResponse entityToResponse(Lead lead);
}
