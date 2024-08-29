package com.giantlink.project.controlers;

import java.util.List;
import java.util.Map;

import com.giantlink.project.helpers.ProductExcelHelper;
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
import com.giantlink.project.models.requests.ProductRequest;
import com.giantlink.project.models.responses.ProductResponse;
import com.giantlink.project.services.ProductService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/product")
@CrossOrigin(origins = { "http://localhost:4200" })
public class ProductController {
	
	@Autowired
	private ProductService productService;

	@GetMapping
	public ResponseEntity<Map<String, Object>> getAll(@RequestParam(defaultValue = "0") int page,
													  @RequestParam(defaultValue = "2") int size,
													  @RequestParam(defaultValue = "", name = "name") String name) 
	{
		Pageable pageable = PageRequest.of(page, size);
		return new ResponseEntity<Map<String,Object>>(productService.getAllPaginations(name, pageable), HttpStatus.OK);
		
	}

	@PostMapping
	public ResponseEntity<ProductResponse> add(@RequestBody ProductRequest productRequest)
			throws GlNotFoundException,GlAlreadyExistException {
		return new ResponseEntity<ProductResponse>(productService.add(productRequest), HttpStatus.CREATED);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getOne(@PathVariable Long id) throws GlNotFoundException {
		return new ResponseEntity<ProductResponse>(productService.get(id), HttpStatus.OK);
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<ProductResponse>> get() throws GlNotFoundException {
		return new ResponseEntity<List<ProductResponse>>(productService.getAll(), HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") Long id) throws GlNotFoundException{
		productService.delete(id);
		return new ResponseEntity<String>("deleted !", HttpStatus.OK);
	}
	

	@PutMapping("/{id}")
	public ResponseEntity<ProductResponse> edit(@PathVariable("id") Long id, @RequestBody ProductRequest productRequest) throws GlAlreadyExistException, GlNotFoundException  {
		return new ResponseEntity<ProductResponse>(productService.update(id, productRequest), HttpStatus.OK);
	}

	@PostMapping("/upload")
	public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file , @RequestParam(name = "sheet", required = false) String sheetNumber) {
		String message = "";
		if (ProductExcelHelper.hasFormat(file)==1) {
			try {
				productService.saveFromExcelToDb(file, sheetNumber);
				message = "Uploaded the file successfully";
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
			} catch (Exception e) {
				message = "Could not upload the file";
				return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
			}
		}else if(ProductExcelHelper.hasFormat(file)==2){
			try{
				productService.saveFromCsvToDb(file);
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
		String filename = "products.xlsx";
		InputStreamResource file = new InputStreamResource(productService.loadFromDbToExcel());
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
				.body(file);
	}

	@GetMapping("/downloadCsv")
	public ResponseEntity<Resource> getFileCsv() {
		String filename = "products.csv";
		InputStreamResource file = new InputStreamResource(productService.loadFromDbToCsv());
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.parseMediaType("text/csv"))
				.body(file);
	}


}
