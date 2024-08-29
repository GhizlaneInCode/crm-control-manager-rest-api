package com.giantlink.project.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.RestTemplate;

import com.giantlink.glintranetdto.models.responses.EmployeeResponse;
import com.giantlink.project.entities.Commercial;
import com.giantlink.project.entities.Ecoute;
import com.giantlink.project.entities.Lead;
import com.giantlink.project.entities.Product;
import com.giantlink.project.entities.Service;
import com.giantlink.project.entities.ServiceType;
import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.mappers.ClientMapper;
import com.giantlink.project.mappers.LeadMapper;
import com.giantlink.project.mappers.ServiceTypeMapper;
import com.giantlink.project.models.requests.LeadRequest;
import com.giantlink.project.models.responses.LeadResponse;
import com.giantlink.project.repositories.ClientRepository;
import com.giantlink.project.repositories.CommercialRepository;
import com.giantlink.project.repositories.EcouteRepository;
import com.giantlink.project.repositories.LeadRepository;
import com.giantlink.project.repositories.ProductRepository;
import com.giantlink.project.repositories.ServiceRepository;
import com.giantlink.project.repositories.ServiceTypeRepository;
import com.giantlink.project.repositories.UserRepository;
import com.giantlink.project.services.LeadService;

@org.springframework.stereotype.Service
public class LeadServiceImpl implements LeadService {

	@Autowired
	private LeadRepository leadRepository;

	@Autowired
	private ServiceRepository serviceRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private CommercialRepository commercialRepository;

	@Autowired
	private ServiceTypeRepository typeRepository;

	@Autowired
	private EcouteRepository ecouteRepository;

	@Autowired
	RestTemplate restTemplate;

	@Transactional
	@Override
	public LeadResponse add(LeadRequest leadRequest) throws GlAlreadyExistException, GlNotFoundException, Exception {

		System.out.println(leadRequest.toString());
		// check user
		/*
		 * Optional<User> findUser = userRepository.findById(leadRequest.getUserId());
		 * if (!findUser.isPresent()) { throw new
		 * GlNotFoundException(leadRequest.getUserId().toString(),
		 * User.class.getSimpleName()); }
		 */

		EmployeeResponse employee = restTemplate.getForObject(
				"http://localhost:8091/api/employee/" + leadRequest.getEmployeeId(), EmployeeResponse.class);

		// check commercial
		Optional<Commercial> findCommercial = commercialRepository.findById(leadRequest.getCommercialId());
		if (!findCommercial.isPresent()) {
			throw new GlNotFoundException(leadRequest.getCommercialId().toString(), Commercial.class.getSimpleName());
		}
		// check product
		Optional<Product> findProduct = productRepository.findById(leadRequest.getProductId());
		if (!findProduct.isPresent()) {
			throw new GlNotFoundException(leadRequest.getProductId().toString(), Product.class.getSimpleName());
		}

		// check ecoute
		Optional<Ecoute> findEcoute = ecouteRepository.findById(leadRequest.getEcouteId());
		if (!findEcoute.isPresent()) {
			throw new GlNotFoundException(leadRequest.getEcouteId().toString(), Ecoute.class.getSimpleName());
		}

		Lead lead = LeadMapper.INSTANCE.requestToEntity(leadRequest);
		lead.setCommercial(findCommercial.get());
		lead.setProduct(findProduct.get());
		lead.setEcoute(findEcoute.get());

		lead.setClient(clientRepository.save(ClientMapper.INSTANCE.requestToEntity(leadRequest.getClient())));
		// lead.setUser(findUser.get());

		// check serviceType
		Optional<ServiceType> findServiceType = typeRepository.findById(leadRequest.getServiceTypeId());
		if (!findServiceType.isPresent()) {
			throw new GlNotFoundException(leadRequest.getServiceTypeId().toString(), ServiceType.class.getSimpleName());
		}

		lead.setServiceTypeId(findServiceType.get().getId());

		// check service
		float totalPoint = 0;

		Set<Service> services = new HashSet<>();
		Set<Long> serviceIds = leadRequest.getServiceIds();
		System.out.println("--------------------" + serviceIds);
		if (serviceIds == null || serviceIds.isEmpty()) {
			throw new GlNotFoundException("list is empty", Service.class.getSimpleName());
		}

		for (Long id : serviceIds) {
			Optional<Service> findService = serviceRepository.findById(id);

			if (findService.isPresent()) {
				services.add(findService.get());
				totalPoint += findService.get().getPoint();

			}
		}

		lead.setServices(services);
		if (totalPoint == 0) {
			throw new Exception("the Total Point must be superior  than ZERO");
		}
		
		lead.setTotalPoint(totalPoint);

		leadRepository.save(lead);
		// BeanUtils.copyProperties(lead , leadResponse);
		LeadResponse leadResponse = LeadMapper.INSTANCE.entityToResponse(lead);
		leadResponse.setEmployee(employee);
		leadResponse.setServiceType(ServiceTypeMapper.INSTANCE.entityToResponse(findServiceType.get()));

		return leadResponse;

	}

	@Override
	public List<LeadResponse> getAll() {

		return LeadMapper.INSTANCE.mapLead(leadRepository.findAll());
	}

	@Override
	public LeadResponse get(Long id) throws GlNotFoundException {

		Optional<Lead> findLead = leadRepository.findById(id);

		if (!findLead.isPresent()) {
			throw new GlNotFoundException(id.toString(), Lead.class.getSimpleName());
		}

		LeadResponse leadResponse = LeadMapper.INSTANCE.entityToResponse(findLead.get());

		EmployeeResponse employee = restTemplate.getForObject(
				"http://localhost:8091/api/employee/" + findLead.get().getEmployeeId(), EmployeeResponse.class);

		Optional<ServiceType> findServiceType = typeRepository.findById(findLead.get().getServiceTypeId());

		leadResponse.setServiceType(ServiceTypeMapper.INSTANCE.entityToResponse(findServiceType.get()));
		leadResponse.setEmployee(employee);

		return leadResponse;

	}

	@Override
	public void delete(Long id) throws GlNotFoundException {

		Optional<Lead> findLead = leadRepository.findById(id);

		if (!findLead.isPresent()) {
			throw new GlNotFoundException(id.toString(), Lead.class.getSimpleName());
		}

		leadRepository.deleteById(id);

	}

	@Override
	public LeadResponse update(Long id, LeadRequest leadRequest) throws GlNotFoundException, Exception {

		Optional<Lead> findLead = leadRepository.findById(id);
		if (!findLead.isPresent()) {
			throw new GlNotFoundException(id.toString(), Lead.class.getSimpleName());
		}

		// check user
//		Optional<User> findUser = userRepository.findById(leadRequest.getEmployeeId());
//		if (!findUser.isPresent()) {
//			throw new GlNotFoundException(leadRequest.getEmployeeId().toString(), User.class.getSimpleName());
//		}

		EmployeeResponse employee = restTemplate.getForObject(
				"http://localhost:8091/api/employee/" + leadRequest.getEmployeeId(), EmployeeResponse.class);

		// check commercial
		Optional<Commercial> findCommercial = commercialRepository.findById(leadRequest.getCommercialId());
		if (!findCommercial.isPresent()) {
			throw new GlNotFoundException(leadRequest.getCommercialId().toString(), Commercial.class.getSimpleName());
		}

		// check product
		Optional<Product> findProduct = productRepository.findById(leadRequest.getProductId());
		if (!findProduct.isPresent()) {
			throw new GlNotFoundException(leadRequest.getProductId().toString(), Product.class.getSimpleName());
		}

		Lead lead = findLead.get();
		lead.setCommercial(findCommercial.get());
		lead.setProduct(findProduct.get());
		lead.setClient(clientRepository.save(ClientMapper.INSTANCE.requestToEntity(leadRequest.getClient())));
		// lead.setUser(findUser.get());

		// check serviceType
		Optional<ServiceType> findServiceType = typeRepository.findById(leadRequest.getServiceTypeId());
		if (!findServiceType.isPresent()) {
			throw new GlNotFoundException(leadRequest.getServiceTypeId().toString(), ServiceType.class.getSimpleName());
		}

		lead.setServiceTypeId(findServiceType.get().getId());

		// check service
		float totalPoint = 0;

		Set<Service> services = new HashSet<>();
		Set<Long> serviceIds = leadRequest.getServiceIds();
		System.out.println("--------------------" + serviceIds);
		if (serviceIds == null || serviceIds.isEmpty()) {
			throw new GlNotFoundException("list is empty", Service.class.getSimpleName());
		}

		for (Long serviceId : serviceIds) {
			Optional<Service> findService = serviceRepository.findById(serviceId);

			if (findService.isPresent()) {
				services.add(findService.get());
				totalPoint += findService.get().getPoint();

			}
		}

		lead.setServices(services);
		if (totalPoint == 0) {
			throw new Exception("the Total Point must be superior  than ZERO");
		}
		lead.setTotalPoint(totalPoint);

		lead.setAppointmentDate(leadRequest.getAppointmentDate());
		lead.setAppointmentTime(leadRequest.getAppointmentTime());

		lead.setCallType(leadRequest.getCallType());
		lead.setVoice(leadRequest.getVoice());
		lead.setAmount(leadRequest.getAmount());
		lead.setCoachValidation(leadRequest.getCoachValidation());
		lead.setEncode(leadRequest.getEncode());
		lead.setComment(leadRequest.getComment());
		lead.setAddedOption(leadRequest.getAddedOption());
		lead.setDeletedOption(leadRequest.getDeletedOption());
		lead.setPrice(leadRequest.getPrice());
		lead.setLeadDate(leadRequest.getLeadDate());

		leadRepository.save(lead);
		// BeanUtils.copyProperties(lead , leadResponse);
		LeadResponse leadResponse = LeadMapper.INSTANCE.entityToResponse(lead);
		leadResponse.setEmployee(employee);
		leadResponse.setServiceType(ServiceTypeMapper.INSTANCE.entityToResponse(findServiceType.get()));

		return leadResponse;
	}

//	@Override
//	public Map<String, Object> getAllPaginations(String name, Pageable pageable) {
//		
////		List<LeadResponse> leadResponses = new ArrayList<>();
////		Page<Lead> leads = null;
////		if (productName.isBlank() && encode.toString().isBlank() && ecoute.isBlank()) {
////			leads = leadRepository.findAll(pageable);
////		} else {
////			leads = leadRepository.findLeadByEncodeAndEcouteAndProductName(encode, ecoute, productName);
////		}
//		
//		List<LeadResponse> leadResponses = new ArrayList<>();
//		Page<Lead> leads = leadRepository.findAll(pageable);
//
//		leads.getContent().forEach(lead -> {
//
//			LeadResponse leadResponse = LeadMapper.INSTANCE.entityToResponse(lead);
//
//			EmployeeResponse employee = restTemplate
//					.getForObject("http://localhost:8091/api/employee/" + lead.getEmployeeId(), EmployeeResponse.class);
//			
//			// check serviceType
//			Optional<ServiceType> findServiceType = typeRepository.findById(lead.getServiceTypeId());
//			
//			leadResponse.setEmployee(employee);
//			leadResponse.setServiceType(ServiceTypeMapper.INSTANCE.entityToResponse(findServiceType.get()));
//			leadResponses.add(leadResponse);
//
//		});
//
//		Map<String, Object> requestResponse = new HashMap<>();
//		requestResponse.put("content", leadResponses);
//		requestResponse.put("currentPage", leads.getNumber());
//		requestResponse.put("totalElements", leads.getTotalElements());
//		requestResponse.put("totalPages", leads.getTotalPages());
//		return requestResponse;
//
//	}

	@Override
	public List<Ecoute> getEcoutes() {

		return ecouteRepository.findAll();
	}

	@Override
	public Ecoute getEcoute(Long id) throws GlNotFoundException {

		Optional<Ecoute> findEcoute = ecouteRepository.findById(id);

		if (!findEcoute.isPresent()) {
			throw new GlNotFoundException(id.toString(), Ecoute.class.getSimpleName());
		}

		return findEcoute.get();
	}

	@Override
	public Map<String, Object> getAllPaginations(String productName, Long ecouteId, Boolean encode, Long employeeId,
			Boolean coachValidation, Pageable pageable) {

		List<LeadResponse> leadResponses = new ArrayList<>();
		Page<Lead> leads = null;

		if (encode == null && productName == null && ecouteId == null && employeeId == null && coachValidation == null)
			leads = leadRepository.findAll(pageable);

		Ecoute ecoute = null;
		Optional<Ecoute> findEcoute;
		if (ecouteId != null) {
			findEcoute = ecouteRepository.findById(ecouteId);
			ecoute = findEcoute.get();
		}

		Product product = null;
		Optional<Product> findProduct;
		// if(!Objects.equals(productName, ""))
		if (productName != null) {
			findProduct = productRepository.findByProductName(productName);
			product = findProduct.get();
		}

		EmployeeResponse findEmployee = null;
		if (employeeId != null) {
			findEmployee = restTemplate.getForObject("http://localhost:8091/api/employee/" + employeeId,
					EmployeeResponse.class);
		}

		leads = leadRepository.findByEncodeAndEcouteAndProduct(encode, ecoute, product, employeeId, coachValidation, pageable);

		leads.getContent().forEach(lead -> {

			LeadResponse leadResponse = LeadMapper.INSTANCE.entityToResponse(lead);

			EmployeeResponse employee = restTemplate
					.getForObject("http://localhost:8091/api/employee/" + lead.getEmployeeId(), EmployeeResponse.class);

			// check serviceType
			Optional<ServiceType> findServiceType = typeRepository.findById(lead.getServiceTypeId());

			leadResponse.setEmployee(employee);
			leadResponse.setServiceType(ServiceTypeMapper.INSTANCE.entityToResponse(findServiceType.get()));
			leadResponses.add(leadResponse);

		});

		Map<String, Object> requestResponse = new HashMap<>();
		requestResponse.put("content", leadResponses);
		requestResponse.put("currentPage", leads.getNumber());
		requestResponse.put("totalElements", leads.getTotalElements());
		requestResponse.put("totalPages", leads.getTotalPages());
		return requestResponse;

	}

	@Override
	public List<LeadResponse> getAllSortedByTotalPoint() {

		List<LeadResponse> leadResponses = new ArrayList<>();
		List<Lead> leads = leadRepository.getSortedDataByTotalPoint();

		leads.forEach(lead -> {

			LeadResponse leadResponse = LeadMapper.INSTANCE.entityToResponse(lead);
			EmployeeResponse employee = restTemplate
					.getForObject("http://localhost:8091/api/employee/" + lead.getEmployeeId(), EmployeeResponse.class);
			leadResponse.setEmployee(employee);
			leadResponses.add(leadResponse);
		});

		return leadResponses;
	}

	@Override
	public List<EmployeeResponse> getAllEmployees() {

		EmployeeResponse[] employees = restTemplate.getForObject("http://localhost:8091/api/employee",
				EmployeeResponse[].class);
		List<EmployeeResponse> responses = Arrays.asList(employees);
		return responses;
	}

	@Override
	public Long getNumberOfLeadsPerDay() {
		
		return leadRepository.getNumberOfLeadsPerDay();
	}

}
