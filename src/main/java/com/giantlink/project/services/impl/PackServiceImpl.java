package com.giantlink.project.services.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import com.giantlink.glintranetdto.models.responses.ProjectResponse;
import com.giantlink.project.helpers.PackExcelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.giantlink.project.entities.Pack;
import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.mappers.PackMapper;
import com.giantlink.project.models.requests.PackRequest;
import com.giantlink.project.models.responses.PackResponse;
import com.giantlink.project.repositories.PackRepository;
import com.giantlink.project.repositories.ProjectRepository;
import com.giantlink.project.services.PackService;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PackServiceImpl implements PackService {

	@Autowired
	PackRepository packRepository;

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	RestTemplate restTemplate;

	@Override
	public PackResponse add(PackRequest packRequest) throws GlAlreadyExistException, GlNotFoundException {
		ProjectResponse projectResponse = restTemplate.getForObject("http://localhost:8091/api/project/" + packRequest.getProjectId(), ProjectResponse.class);
		Optional<Pack> findPack = packRepository.findByPackName(packRequest.getPackName());
		if (findPack.isPresent()) {
			throw new GlAlreadyExistException(packRequest.getPackName(), Pack.class.getSimpleName());
		}
		Pack pack = PackMapper.INSTANCE.requestToEntity(packRequest);
		packRepository.save(pack);
		PackResponse packResponse = PackMapper.INSTANCE.entityToResponse(pack);
		packResponse.setProject(projectResponse);
		return packResponse;
	}

	@Override
	public List<PackResponse> getAll() {
		return PackMapper.INSTANCE.mapPack(packRepository.findAll());
	}

	@Override
	public PackResponse get(Long id) throws GlNotFoundException {
		Optional<Pack> findPack = packRepository.findById(id);
		if (findPack.isEmpty()) {
			throw new GlNotFoundException("pack", Pack.class.getSimpleName());
		}
		ProjectResponse projectResponse = restTemplate.getForObject("http://localhost:8091/api/project/" + findPack.get().getProjectId(), ProjectResponse.class);
		PackResponse response = PackMapper.INSTANCE.entityToResponse(findPack.get());
		response.setProject(projectResponse);
		return response;
	}

	@Override
	public void delete(Long id) throws GlNotFoundException {
		Optional<Pack> findPack = packRepository.findById(id);
		if (findPack.isEmpty()) {
			throw new GlNotFoundException("pack", Pack.class.getSimpleName());
		}
		packRepository.delete(findPack.get());
	}

	@Override
	public PackResponse update(Long id, PackRequest packRequest) throws GlNotFoundException {
		ProjectResponse projectResponse = restTemplate.getForObject("http://localhost:8091/api/project/" + packRequest.getProjectId(), ProjectResponse.class);
		Optional<Pack> findPack = packRepository.findById(id);
		if (findPack.isEmpty()) {
			throw new GlNotFoundException("pack", Pack.class.getSimpleName());
		}
		Pack pack = PackMapper.INSTANCE.requestToEntity(packRequest);
		pack.setId(id);
		packRepository.save(pack);
		PackResponse packResponse = PackMapper.INSTANCE.entityToResponse(pack);
		packResponse.setProject(projectResponse);
		return packResponse;
	}

	@Override
	public Map<String, Object> getAllPaginations(String name, Pageable pageable) {
		List<PackResponse> packResponseList = new ArrayList<>();
		ResponseEntity<ProjectResponse[]> result = restTemplate.getForEntity("http://localhost:8091/api/project",ProjectResponse[].class);
		List<ProjectResponse> projects = List.of(result.getBody());
		if(name.isBlank()){
			for (Pack pack : packRepository.findAll(pageable).getContent()) {
				PackResponse packResponse = PackMapper.INSTANCE.entityToResponse(pack);
				ProjectResponse projectResponse = projects.stream().filter(project -> project.getId() == pack.getProjectId()).findFirst().get();
				packResponse.setProject(projectResponse);
				packResponseList.add(packResponse);
			}
		}else
			for (Pack pack : packRepository.findByPackNameContainingIgnoreCase(name, pageable).getContent()) {
				PackResponse packResponse = PackMapper.INSTANCE.entityToResponse(pack);
				ProjectResponse projectResponse = projects.stream().filter(project -> project.getId() == pack.getProjectId()).findFirst().get();
				packResponse.setProject(projectResponse);
				packResponseList.add(packResponse);
			}

		Page<Pack> packs = (name.isBlank()) ? packRepository.findAll(pageable)
				: packRepository.findByPackNameContainingIgnoreCase(name, pageable);
		Map<String, Object> requestResponse = new HashMap<>();
		requestResponse.put("content", packResponseList);
		requestResponse.put("currentPage", packs.getNumber());
		requestResponse.put("totalElements", packs.getTotalElements());
		requestResponse.put("totalPages", packs.getTotalPages());
		return requestResponse;
	}

	@Override
	public void saveFromExcelToDb(MultipartFile file, String sheetNumber) {
		try{
			List<Pack> packs = PackExcelHelper.excelToPacks(file.getInputStream(), sheetNumber);
			packRepository.saveAll(packs);
		} catch (IOException e) {
			throw new RuntimeException("fail to store excel data: " + e.getMessage());
		}
	}

	@Override
	public void saveFromCsvToDb(MultipartFile file) {
		try{
			List<Pack> packs = PackExcelHelper.csvToPacks(file.getInputStream());
			packRepository.saveAll(packs);
		} catch (IOException e) {
			throw new RuntimeException("fail to store csv data: " + e.getMessage());
		}
	}

	@Override
	public ByteArrayInputStream loadFromDbToExcel() {
		List<Pack> packs = packRepository.findAll();
		ByteArrayInputStream in = PackExcelHelper.packsToExcel(packs);
		return in;
	}
	@Override
	public ByteArrayInputStream loadFromDbToCsv() {
		List<Pack> packs = packRepository.findAll();
		ByteArrayInputStream in = PackExcelHelper.packsToCsv(packs);
		return in;
	}

}
