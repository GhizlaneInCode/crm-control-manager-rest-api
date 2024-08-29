package com.giantlink.project.mappers;

import java.util.List;
import java.util.Set;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.giantlink.project.entities.ServiceType;
import com.giantlink.project.models.requests.ServiceTypeRequest;
import com.giantlink.project.models.responses.ServiceTypeResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ServiceTypeMapper {

	ServiceTypeMapper INSTANCE = Mappers.getMapper(ServiceTypeMapper.class);

	List<ServiceTypeResponse> mapServiceType(List<ServiceType> types);

	Set<ServiceTypeResponse> mapServiceType(Set<ServiceType> types);

	ServiceType requestToEntity(ServiceTypeRequest typeRequest);

	ServiceTypeResponse entityToResponse(ServiceType type);

}
