package com.giantlink.project.services.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.giantlink.glintranetdto.models.responses.ProjectResponse;
import com.giantlink.project.entities.Pack;
import com.giantlink.project.helpers.OptionExcelHelper;
import com.giantlink.project.helpers.PackExcelHelper;
import com.giantlink.project.mappers.PackMapper;
import com.giantlink.project.models.responses.PackResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.giantlink.project.entities.Option;
import com.giantlink.project.entities.Project;
import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.mappers.OptionMapper;
import com.giantlink.project.models.requests.OptionRequest;
import com.giantlink.project.models.responses.OptionResponse;
import com.giantlink.project.repositories.OptionRepository;
import com.giantlink.project.repositories.ProjectRepository;
import com.giantlink.project.services.OptionService;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class OptionServiceImpl implements OptionService {

	@Autowired
	private OptionRepository optionRepository;

	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	RestTemplate restTemplate;
	@Override
	public OptionResponse add(OptionRequest optionRequest) throws GlAlreadyExistException, GlNotFoundException {

		Optional<Option> findOption = optionRepository.findByOptionName(optionRequest.getOptionName());
		ProjectResponse projectResponse = restTemplate.getForObject("http://localhost:8091/api/project/" + optionRequest.getProjectId(), ProjectResponse.class);

		if (findOption.isPresent()) {
			throw new GlAlreadyExistException(optionRequest.getOptionName(), Option.class.getSimpleName());
		}
		Option option = OptionMapper.INSTANCE.requestToEntity(optionRequest);
		optionRepository.save(option);
		OptionResponse optionResponse = OptionMapper.INSTANCE.entityToResponse(option);
		optionResponse.setProject(projectResponse);
		return optionResponse;

	}

	@Override
	public List<OptionResponse> getAll() {
		return OptionMapper.INSTANCE.mapOption(optionRepository.findAll());
	}

	@Override
	public OptionResponse get(Long id) throws GlNotFoundException {
		Optional<Option> findOption = optionRepository.findById(id);
		if (!findOption.isPresent()) {
			throw new GlNotFoundException(id.toString(), Option.class.getSimpleName());
		}
		ProjectResponse projectResponse = restTemplate.getForObject("http://localhost:8091/api/project/" + findOption.get().getProjectId(), ProjectResponse.class);
		OptionResponse response = OptionMapper.INSTANCE.entityToResponse(findOption.get());
		response.setProject(projectResponse);
		return response;
	}

	@Override
	public void delete(Long id) throws GlNotFoundException {
		Optional<Option> findOption = optionRepository.findById(id);
		if (!findOption.isPresent()) {
			throw new GlNotFoundException(id.toString(), Option.class.getSimpleName());
		}
		optionRepository.deleteById(id);
	}

	@Override
	public OptionResponse update(Long id, OptionRequest optionRequest) throws GlNotFoundException {
		Optional<Option> findOption = optionRepository.findById(id);
		ProjectResponse projectResponse = restTemplate.getForObject("http://localhost:8091/api/project/" + optionRequest.getProjectId(), ProjectResponse.class);
		System.out.println(projectResponse);
		if (!findOption.isPresent()) {
			throw new GlNotFoundException(id.toString(), Option.class.getSimpleName());
		}
		Option option = OptionMapper.INSTANCE.requestToEntity(optionRequest);
		option.setId(id);
		optionRepository.save(option);
		OptionResponse optionResponse = OptionMapper.INSTANCE.entityToResponse(option);
		optionResponse.setProject(projectResponse);
		return optionResponse;
	}

	@Override
	public Map<String, Object> getAllPaginations(String name, Pageable pageable) {
		List<OptionResponse> optionList = new ArrayList<>();
		ResponseEntity<ProjectResponse[]> result = restTemplate.getForEntity("http://localhost:8091/api/project",ProjectResponse[].class);
		List<ProjectResponse> projects = List.of(result.getBody());
		if(name.isBlank()){
			for (Option option : optionRepository.findAll(pageable).getContent()) {
				OptionResponse optionResponse = OptionMapper.INSTANCE.entityToResponse(option);
				ProjectResponse projectResponse = projects.stream().filter(project -> project.getId() == option.getProjectId()).findFirst().get();
				optionResponse.setProject(projectResponse);
				optionList.add(optionResponse);
			}
		}else
			for (Option option : optionRepository.findByOptionNameContainingIgnoreCase(name, pageable).getContent()) {
				OptionResponse optionResponse = OptionMapper.INSTANCE.entityToResponse(option);
				ProjectResponse projectResponse = projects.stream().filter(project -> project.getId() == option.getProjectId()).findFirst().get();
				optionResponse.setProject(projectResponse);
				optionList.add(optionResponse);
			}

		Page<Option> options = (name.isBlank()) ? optionRepository.findAll(pageable)
				: optionRepository.findByOptionNameContainingIgnoreCase(name, pageable);
		Map<String, Object> requestResponse = new HashMap<>();
		requestResponse.put("content", optionList);
		requestResponse.put("currentPage", options.getNumber());
		requestResponse.put("totalElements", options.getTotalElements());
		requestResponse.put("totalPages", options.getTotalPages());
		return requestResponse;
	}

	@Override
	public void saveFromExcelToDb(MultipartFile file, String sheetNumber) {
		try{
			List<Option> options = OptionExcelHelper.excelToOptions(file.getInputStream(), sheetNumber);
			optionRepository.saveAll(options);
		} catch (IOException e) {
			throw new RuntimeException("fail to store excel data: " + e.getMessage());
		}
	}

	@Override
	public void saveFromCsvToDb(MultipartFile file) {
		try{
			List<Option> options = OptionExcelHelper.csvToOptions(file.getInputStream());
			optionRepository.saveAll(options);
		} catch (IOException e) {
			throw new RuntimeException("fail to store excel data: " + e.getMessage());
		}
	}

	@Override
	public ByteArrayInputStream loadFromDbToExcel() {
		List<Option> options = optionRepository.findAll();
		ByteArrayInputStream in = OptionExcelHelper.optionsToExcel(options);
		return in;
	}

	@Override
	public ByteArrayInputStream loadFromDbToCsv() {
		List<Option> options = optionRepository.findAll();
		ByteArrayInputStream in = OptionExcelHelper.optionsToCsv(options);
		return in;
	}

}
