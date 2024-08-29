package com.giantlink.project.controlers;

import java.util.List;
import java.util.Map;

import com.giantlink.project.models.responses.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.models.requests.ServiceRequest;
import com.giantlink.project.models.responses.ServiceResponse;
import com.giantlink.project.models.responses.TopService;
import com.giantlink.project.services.ServiceService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/service")
@CrossOrigin(origins = { "http://localhost:4200" })
public class ServiceController {
	
	@Autowired
	private ServiceService service;

	@GetMapping
	public ResponseEntity<Map<String, Object>> getAll(@RequestParam(defaultValue = "0") int page,
													  @RequestParam(defaultValue = "2") int size,
													  @RequestParam(defaultValue = "", name = "name") String name) 
	{
		Pageable pageable = PageRequest.of(page, size);
		return new ResponseEntity<Map<String,Object>>(service.getAllPaginations(name, pageable), HttpStatus.OK);
		
	}

	@PostMapping
	public ResponseEntity<ServiceResponse> add(@RequestBody ServiceRequest serviceRequest)
			throws GlNotFoundException,GlAlreadyExistException {
		return new ResponseEntity<ServiceResponse>(service.add(serviceRequest), HttpStatus.CREATED);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ServiceResponse> getOne(@PathVariable Long id) throws GlNotFoundException {
		return new ResponseEntity<ServiceResponse>(service.get(id), HttpStatus.OK);
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<ServiceResponse>> get() throws GlNotFoundException {
		return new ResponseEntity<List<ServiceResponse>>(service.getAll(), HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") Long id) throws GlNotFoundException{
		service.delete(id);
		return new ResponseEntity<String>("deleted !", HttpStatus.OK);
	}
	

	@PutMapping("/{id}")
	public ResponseEntity<ServiceResponse> edit(@PathVariable("id") Long id, @RequestBody ServiceRequest serviceRequest) throws GlAlreadyExistException, GlNotFoundException  {
		return new ResponseEntity<ServiceResponse>(service.update(id, serviceRequest), HttpStatus.OK);
	}
	
	@GetMapping("/topServices")
	public ResponseEntity<List<ServiceResponse>> getTopServices() throws GlNotFoundException {
		return new ResponseEntity<List<ServiceResponse>>(service.getTopServices(), HttpStatus.OK);
	}
	
	@GetMapping("/leadsNumber/{id}")
	public ResponseEntity<TopService> getLeadsNumber(@PathVariable("id") Long id) throws GlNotFoundException {
		return new ResponseEntity<TopService>(service.getLeadsNumber(id), HttpStatus.OK);
	}
	
	@GetMapping("/numberOfSoldServices")
	public ResponseEntity<Long> getNumberOfSoldServicesPerDay() throws GlNotFoundException {
		return new ResponseEntity<Long>(service.getNumberOfSoldServicesPerDay(), HttpStatus.OK);
	}
	
	
	@GetMapping("/totalPointsOfSoldServicesPerDay")
	public ResponseEntity<Float> getTotalPointsOfSoldServicesPerDay() throws GlNotFoundException {
		return new ResponseEntity<Float>(service.getTotalPointsOfSoldServicesPerDay(), HttpStatus.OK);
	}
	
	


}
