package com.giantlink.project.services;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.models.requests.CommercialRequest;
import com.giantlink.project.models.responses.CommercialResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CommercialService {
	
	CommercialResponse add(CommercialRequest commercialRequest) throws GlAlreadyExistException, GlNotFoundException;

	List<CommercialResponse> getAll();

	CommercialResponse get(Long id) throws GlNotFoundException;

	void delete(Long id) throws GlNotFoundException;

	CommercialResponse update(Long id, CommercialRequest commercialRequest) throws GlNotFoundException ;
	
	void changeStatus(Long id,Boolean status) throws GlNotFoundException;
	
	Map<String, Object> getAllPaginations(String name, Pageable pageable);

	void saveFromExcelToDb(MultipartFile file, String sheetNumber);
	void saveFromCsvToDb(MultipartFile file);
	ByteArrayInputStream loadFromDbToExcel();

	ByteArrayInputStream loadFromDbToCsv();
}
