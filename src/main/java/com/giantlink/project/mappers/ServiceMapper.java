package com.giantlink.project.mappers;

import java.util.List;
import java.util.Set;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.giantlink.project.entities.Service;
import com.giantlink.project.models.requests.ServiceRequest;
import com.giantlink.project.models.responses.ServiceResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ServiceMapper {
	
	ServiceMapper INSTANCE = Mappers.getMapper(ServiceMapper.class);

	List<ServiceResponse> mapService(List<Service> services);
	
	Set<ServiceResponse> mapService(Set<Service> services);
	
	Set<Service> mapRequestToEntity(Set<ServiceRequest> services);
	
	Service requestToEntity(ServiceRequest serviceRequest);

	ServiceResponse entityToResponse(Service service);

}
