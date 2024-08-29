package com.giantlink.project.services;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.giantlink.glintranetdto.models.responses.EmployeeResponse;
import com.giantlink.project.entities.Ecoute;
import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.models.requests.LeadRequest;
import com.giantlink.project.models.responses.LeadResponse;

public interface LeadService {

	LeadResponse add(LeadRequest leadRequest) throws GlAlreadyExistException, GlNotFoundException, Exception;

	List<LeadResponse> getAll();

	List<Ecoute> getEcoutes();

	Ecoute getEcoute(Long id) throws GlNotFoundException;

	LeadResponse get(Long id) throws GlNotFoundException;

	void delete(Long id) throws GlNotFoundException;

	LeadResponse update(Long id, LeadRequest leadRequest) throws GlNotFoundException, Exception;

	List<LeadResponse> getAllSortedByTotalPoint();

	List<EmployeeResponse> getAllEmployees();
	
	Long getNumberOfLeadsPerDay();

	// Map<String, Object> getAllPaginations(String name, Pageable pageable);

	Map<String, Object> getAllPaginations(String productName, Long ecoute, Boolean encode, Long employeeId,
			Boolean coachValidation, Pageable pageable);

}
