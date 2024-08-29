package com.giantlink.project.services;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.models.requests.OptionRequest;
import com.giantlink.project.models.responses.OptionResponse;
import org.springframework.web.multipart.MultipartFile;

public interface OptionService {
	
	OptionResponse add(OptionRequest optionRequest) throws GlAlreadyExistException, GlNotFoundException;

	List<OptionResponse> getAll();

	OptionResponse get(Long id) throws GlNotFoundException;

	void delete(Long id) throws GlNotFoundException;

	OptionResponse update(Long id, OptionRequest optionRequest) throws GlNotFoundException ;
	
	Map<String, Object> getAllPaginations(String name, Pageable pageable);

	void saveFromExcelToDb(MultipartFile file, String sheetNumber);
	void saveFromCsvToDb(MultipartFile file);
	ByteArrayInputStream loadFromDbToExcel();

	ByteArrayInputStream loadFromDbToCsv();
}
