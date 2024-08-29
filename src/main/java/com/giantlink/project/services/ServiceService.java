package com.giantlink.project.services;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.models.requests.ServiceRequest;
import com.giantlink.project.models.responses.ServiceResponse;
import com.giantlink.project.models.responses.TopService;

public interface ServiceService {
	
	ServiceResponse add(ServiceRequest serviceRequest) throws GlAlreadyExistException, GlNotFoundException;

	List<ServiceResponse> getAll();

	ServiceResponse get(Long id) throws GlNotFoundException;

	void delete(Long id) throws GlNotFoundException;

	ServiceResponse update(Long id, ServiceRequest serviceRequest) throws GlNotFoundException ;
	
	List<ServiceResponse> getTopServices();
	
	Map<String, Object> getAllPaginations(String name, Pageable pageable);

	TopService getLeadsNumber(Long id);
	
	Long getNumberOfSoldServicesPerDay();
	
	Float getTotalPointsOfSoldServicesPerDay();

}
