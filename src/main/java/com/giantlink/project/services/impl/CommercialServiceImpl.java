package com.giantlink.project.services.impl;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.giantlink.project.helpers.CommercialExcelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.giantlink.project.entities.Commercial;
import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.mappers.CommercialMapper;
import com.giantlink.project.models.requests.CommercialRequest;
import com.giantlink.project.models.responses.CommercialResponse;
import com.giantlink.project.repositories.CommercialRepository;
import com.giantlink.project.services.CommercialService;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CommercialServiceImpl implements CommercialService{
	
	@Autowired
	private CommercialRepository commercialRepository;

	@Override
	public CommercialResponse add(CommercialRequest commercialRequest)
			throws GlAlreadyExistException, GlNotFoundException {
		
		Optional<Commercial> findCommercial = commercialRepository.findByCommercialName(commercialRequest.getCommercialName());

		if (findCommercial.isPresent()) {
			throw new GlAlreadyExistException(commercialRequest.getCommercialName(), Commercial.class.getSimpleName());
		}
		return CommercialMapper.INSTANCE
				.entityToResponse(commercialRepository.save(CommercialMapper.INSTANCE.requestToEntity(commercialRequest)));

	}

	@Override
	public List<CommercialResponse> getAll() {
		
		return CommercialMapper.INSTANCE.mapCommercial(commercialRepository.findAll());
	}

	@Override
	public CommercialResponse get(Long id) throws GlNotFoundException {
		
		Optional<Commercial> findCommercial = commercialRepository.findById(id);

		if (!findCommercial.isPresent()) {
			throw new GlNotFoundException(id.toString(), Commercial.class.getSimpleName());
		}

		return CommercialMapper.INSTANCE.entityToResponse(commercialRepository.findById(id).get());
	}

	@Override
	public void delete(Long id) throws GlNotFoundException {
		
		Optional<Commercial> findCommercial = commercialRepository.findById(id);

		if (!findCommercial.isPresent()) {
			throw new GlNotFoundException(id.toString(), Commercial.class.getSimpleName());
		} 
		
		commercialRepository.deleteById(id);
		
	}

	@Override
	public CommercialResponse update(Long id, CommercialRequest commercialRequest) throws GlNotFoundException {
		
		Optional<Commercial> findCommercial = commercialRepository.findById(id);

		if (!findCommercial.isPresent()) {
			throw new GlNotFoundException(id.toString(), Commercial.class.getSimpleName());
		}
		
		Commercial commercial = commercialRepository.findById(id).get();

		commercial.setCommercialName(commercialRequest.getCommercialName());
		commercial.setStatut(commercialRequest.getStatut());
		

		commercialRepository.save(commercial);

		return CommercialMapper.INSTANCE.entityToResponse(commercial);
	}

	@Override
	public Map<String, Object> getAllPaginations(String name, Pageable pageable) {
		
		List<CommercialResponse> commercialList = new ArrayList<>();
		Page<Commercial> commercials = (name.isBlank()) ? commercialRepository.findAll(pageable)
				: commercialRepository.findByCommercialNameContainingIgnoreCase(name, pageable);
		commercials.getContent().forEach(commercial -> {
			CommercialResponse response = CommercialResponse.builder().id(commercial.getId())
					.commercialName(commercial.getCommercialName())
					.statut(commercial.getStatut())
					.build();
			
			commercialList.add(response);
		});
		Map<String, Object> commercialMap = new HashMap<>();
		commercialMap.put("content", commercialList);
		commercialMap.put("currentPage", commercials.getNumber());
		commercialMap.put("totalElements", commercials.getTotalElements());
		commercialMap.put("totalPages", commercials.getTotalPages());

		return commercialMap;
	}

	@Override
	public void saveFromExcelToDb(MultipartFile file, String sheetNumber) {
		try {
			List<Commercial> commercials = CommercialExcelHelper.excelToCommercial(file.getInputStream(), sheetNumber);
			commercialRepository.saveAll(commercials);
		} catch (Exception e) {
			throw new RuntimeException("fail to store excel data: " + e.getMessage());
		}
	}

	@Override
	public void saveFromCsvToDb(MultipartFile file) {
		try {
			List<Commercial> commercials = CommercialExcelHelper.csvToCommercial(file.getInputStream());
			commercialRepository.saveAll(commercials);
		} catch (Exception e) {
			throw new RuntimeException("fail to store csv data: " + e.getMessage());
		}
	}

	@Override
	public ByteArrayInputStream loadFromDbToExcel() {
		List<Commercial> commercials = commercialRepository.findAll();
		ByteArrayInputStream in = CommercialExcelHelper.CommercialToExcel(commercials);
		return in;
	}

	@Override
	public ByteArrayInputStream loadFromDbToCsv() {
		List<Commercial> commercials = commercialRepository.findAll();
		ByteArrayInputStream in = CommercialExcelHelper.CommercialToCsv(commercials);
		return in;
	}

	@Override
	public void changeStatus(Long id, Boolean status) throws GlNotFoundException {
		Optional<Commercial> commercial = commercialRepository.findById(id);
		if (!commercial.isPresent()) {
			throw new GlNotFoundException(id.toString(), Commercial.class.getSimpleName());
		}
		commercial.get().setStatut(status);
		commercialRepository.save(commercial.get());
		
	}

}
