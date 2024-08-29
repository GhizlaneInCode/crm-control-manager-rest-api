package com.giantlink.project.services.impl;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.giantlink.project.helpers.ServiceTypeExcelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.giantlink.project.entities.ServiceType;
import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.mappers.ServiceMapper;
import com.giantlink.project.mappers.ServiceTypeMapper;
import com.giantlink.project.models.requests.ServiceTypeRequest;
import com.giantlink.project.models.responses.ServiceTypeResponse;
import com.giantlink.project.repositories.ServiceTypeRepository;
import com.giantlink.project.services.ServiceTypeService;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ServiceTypeServiceImpl implements ServiceTypeService {

	@Autowired
	private ServiceTypeRepository typeRepository;

	@Override
	public ServiceTypeResponse add(ServiceTypeRequest serviceTypeRequest)
			throws GlAlreadyExistException, GlNotFoundException {

		Optional<ServiceType> findType = typeRepository.findByLabel(serviceTypeRequest.getLabel());

		if (findType.isPresent()) {
			throw new GlAlreadyExistException(serviceTypeRequest.getLabel(), ServiceType.class.getSimpleName());
		}
		return ServiceTypeMapper.INSTANCE
				.entityToResponse(typeRepository.save(ServiceTypeMapper.INSTANCE.requestToEntity(serviceTypeRequest)));

	}

	@Override
	public List<ServiceTypeResponse> getAll() {
		return ServiceTypeMapper.INSTANCE.mapServiceType(typeRepository.findAll());
	}

	@Override
	public ServiceTypeResponse get(Long id) throws GlNotFoundException {

		Optional<ServiceType> findType = typeRepository.findById(id);

		if (!findType.isPresent()) {
			throw new GlNotFoundException(id.toString(), ServiceType.class.getSimpleName());
		}

		return ServiceTypeMapper.INSTANCE.entityToResponse(typeRepository.findById(id).get());

	}

	@Override
	public ServiceTypeResponse get(String label) throws GlNotFoundException {
		Optional<ServiceType> findType = typeRepository.findByLabel(label);

		if (!findType.isPresent()) {
			throw new GlNotFoundException(label, ServiceType.class.getSimpleName());
		}

		return ServiceTypeMapper.INSTANCE.entityToResponse(findType.get());
	}

	@Override
	public void delete(Long id) throws GlNotFoundException {

		Optional<ServiceType> findType = typeRepository.findById(id);

		if (!findType.isPresent()) {
			throw new GlNotFoundException(id.toString(), ServiceType.class.getSimpleName());
		}

		typeRepository.deleteById(id);
	}

	@Override
	public ServiceTypeResponse update(Long id, ServiceTypeRequest serviceTypeRequest) throws GlNotFoundException {

		Optional<ServiceType> findType = typeRepository.findById(id);

		if (!findType.isPresent()) {
			throw new GlNotFoundException(id.toString(), ServiceType.class.getSimpleName());
		}

		ServiceType serviceType = typeRepository.findById(id).get();

		serviceType.setLabel(serviceTypeRequest.getLabel());

		typeRepository.save(serviceType);

		return ServiceTypeMapper.INSTANCE.entityToResponse(serviceType);

	}

	@Override
	public Map<String, Object> getAllPaginations(String name, Pageable pageable) {

		List<ServiceTypeResponse> typeList = new ArrayList<>();
		Page<ServiceType> types = (name.isBlank()) ? typeRepository.findAll(pageable)
				: typeRepository.findByLabelContainingIgnoreCase(name, pageable);
		types.getContent().forEach(type -> {
			ServiceTypeResponse response = ServiceTypeResponse.builder().id(type.getId()).label(type.getLabel())
					.services(ServiceMapper.INSTANCE.mapService(type.getServices())).build();

			typeList.add(response);
		});
		Map<String, Object> typeMap = new HashMap<>();
		typeMap.put("content", typeList);
		typeMap.put("currentPage", types.getNumber());
		typeMap.put("totalElements", types.getTotalElements());
		typeMap.put("totalPages", types.getTotalPages());

		return typeMap;
	}

	@Override
	public void saveFromExcelToDb(MultipartFile file, String sheetNumber) {
		try {
			List<ServiceType> serviceTypes = ServiceTypeExcelHelper.excelToServiceType(file.getInputStream(), sheetNumber);
			typeRepository.saveAll(serviceTypes);
		} catch (Exception e) {
			throw new RuntimeException("fail to store excel data: " + e.getMessage());
		}
	}

	@Override
	public void saveFromCsvToDb(MultipartFile file) {
       try{
		   List<ServiceType> serviceTypes = ServiceTypeExcelHelper.csvToServiceType(file.getInputStream());
		   typeRepository.saveAll(serviceTypes);
	   }catch (Exception e){
		   throw new RuntimeException("fail to store csv data: " + e.getMessage());
	   }
	}

	@Override
	public ByteArrayInputStream loadFromDbToExcel() {
		List<ServiceType> serviceTypes = typeRepository.findAll();
		ByteArrayInputStream in = ServiceTypeExcelHelper.ServiceTypeToExcel(serviceTypes);
		return in;
	}

	@Override
	public ByteArrayInputStream loadFromDbToCsv() {
		List<ServiceType> serviceTypes = typeRepository.findAll();
		ByteArrayInputStream in = ServiceTypeExcelHelper.ServiceTypeToCsv(serviceTypes);
		return in;
	}

}
