package com.giantlink.project.controlers;

import java.util.List;
import java.util.Map;

import com.giantlink.project.helpers.CommercialExcelHelper;
import com.giantlink.project.helpers.OptionExcelHelper;
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
import com.giantlink.project.models.requests.CommercialRequest;
import com.giantlink.project.models.responses.CommercialResponse;
import com.giantlink.project.services.CommercialService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/commercial")
@CrossOrigin(origins = { "http://localhost:4200" })
public class CommercialController {
	
	@Autowired
	private  CommercialService commercialService;

	@GetMapping
	public ResponseEntity<Map<String, Object>> getAll(@RequestParam(defaultValue = "0") int page,
													  @RequestParam(defaultValue = "2") int size,
													  @RequestParam(defaultValue = "", name = "name") String name) 
	{
		Pageable pageable = PageRequest.of(page, size);
		return new ResponseEntity<Map<String,Object>>(commercialService.getAllPaginations(name, pageable), HttpStatus.OK);
		
	}

	@PostMapping
	public ResponseEntity<CommercialResponse> add(@RequestBody CommercialRequest commercialRequest)
			throws GlNotFoundException,GlAlreadyExistException {
		return new ResponseEntity<CommercialResponse>(commercialService.add(commercialRequest), HttpStatus.CREATED);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<CommercialResponse> getOne(@PathVariable Long id) throws GlNotFoundException {
		return new ResponseEntity<CommercialResponse>(commercialService.get(id), HttpStatus.OK);
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<CommercialResponse>> get() throws GlNotFoundException {
		return new ResponseEntity<List<CommercialResponse>>(commercialService.getAll(), HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") Long id) throws GlNotFoundException{
		commercialService.delete(id);
		return new ResponseEntity<String>("deleted !", HttpStatus.OK);
	}
	

	@PutMapping("/{id}")
	public ResponseEntity<CommercialResponse> edit(@PathVariable("id") Long id, @RequestBody CommercialRequest commercialRequest) throws GlAlreadyExistException, GlNotFoundException  {
		return new ResponseEntity<CommercialResponse>(commercialService.update(id, commercialRequest), HttpStatus.OK);
	}
	
	@PutMapping("/status/{id}")
	public ResponseEntity<String> changeStatus(@PathVariable("id") Long id, @RequestBody Boolean status)
			throws GlNotFoundException {
		commercialService.changeStatus(id, status);
		return new ResponseEntity<String>("status changed!", HttpStatus.OK);
	}

	@PostMapping("/upload")
	public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file , @RequestParam(name = "sheet", required = false) String sheetNumber) {
		String message = "";
		if (CommercialExcelHelper.hasFormat(file)==1) {
			try {
				commercialService.saveFromExcelToDb(file, sheetNumber);
				message = "Uploaded the file successfully";
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
			} catch (Exception e) {
				message = "Could not upload the file";
				return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
			}
		}else if(CommercialExcelHelper.hasFormat(file)==2){
			try{
				commercialService.saveFromCsvToDb(file);
				message = "Uploaded the file successfully";
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
			}catch (Exception e){
				message = "Could not upload the file";
				return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
			}
		}
		message = "Please upload an excel or csv file!";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
	}

	@GetMapping("/downloadExcel")
	public ResponseEntity<Resource> getFileExcel() {
		String filename = "commercials.xlsx";
		InputStreamResource file = new InputStreamResource(commercialService.loadFromDbToExcel());
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
				.body(file);
	}

	@GetMapping("/downloadCsv")
	public ResponseEntity<Resource> getFileCsv() {
		String filename = "commercials.csv";
		InputStreamResource file = new InputStreamResource(commercialService.loadFromDbToCsv());
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.parseMediaType("text/csv"))
				.body(file);
	}


}
