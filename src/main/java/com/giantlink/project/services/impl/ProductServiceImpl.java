package com.giantlink.project.services.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.giantlink.project.helpers.ProductExcelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.giantlink.project.entities.Product;
import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.mappers.ProductMapper;
import com.giantlink.project.models.requests.ProductRequest;
import com.giantlink.project.models.responses.ProductResponse;
import com.giantlink.project.repositories.ProductRepository;
import com.giantlink.project.services.ProductService;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductServiceImpl implements ProductService{
	
	@Autowired
	private ProductRepository productRepository;

	@Override
	public ProductResponse add(ProductRequest productRequest) throws GlAlreadyExistException, GlNotFoundException {
		
		Optional<Product> findProduct = productRepository.findByProductName(productRequest.getProductName());

		if (findProduct.isPresent()) {
			throw new GlAlreadyExistException(productRequest.getProductName(), Product.class.getSimpleName());
		}
		return ProductMapper.INSTANCE
				.entityToResponse(productRepository.save(ProductMapper.INSTANCE.requestToEntity(productRequest)));

	}

	@Override
	public List<ProductResponse> getAll() {
		
		return ProductMapper.INSTANCE.mapProduct(productRepository.findAll());
	}

	@Override
	public ProductResponse get(Long id) throws GlNotFoundException {
		
		Optional<Product> findProduct = productRepository.findById(id);

		if (!findProduct.isPresent()) {
			throw new GlNotFoundException(id.toString(), Product.class.getSimpleName());
		}

		return ProductMapper.INSTANCE.entityToResponse(productRepository.findById(id).get());
		
	}

	@Override
	public void delete(Long id) throws GlNotFoundException {
		
		Optional<Product> findProduct = productRepository.findById(id);

		if (!findProduct.isPresent()) {
			throw new GlNotFoundException(id.toString(), Product.class.getSimpleName());
		} 
		
		productRepository.deleteById(id);
	}

	@Override
	public ProductResponse update(Long id, ProductRequest productRequest) throws GlNotFoundException, GlAlreadyExistException {
		
		Optional<Product> findProduct = productRepository.findById(id);

		if (!findProduct.isPresent()) {
			throw new GlNotFoundException(id.toString(), Product.class.getSimpleName());
		}
		
		Optional<Product> findProductByName = productRepository.findByProductName(productRequest.getProductName());

		if (findProductByName.isPresent()) {
			throw new GlAlreadyExistException(productRequest.getProductName(), Product.class.getSimpleName());
		}
		
		Product product = findProduct.get();
		product.setProductName(productRequest.getProductName());
		return ProductMapper.INSTANCE.entityToResponse(productRepository.save(product));
	}

	@Override
	public Map<String, Object> getAllPaginations(String name, Pageable pageable) {
		
		List<ProductResponse> productList = new ArrayList<>();
		Page<Product> products = (name.isBlank()) ? productRepository.findAll(pageable)
				: productRepository.findByProductNameContainingIgnoreCase(name, pageable);
		products.getContent().forEach(product -> {
			ProductResponse response = ProductResponse.builder().id(product.getId())
					.productName(product.getProductName())
					.build();
			
			productList.add(response);
		});
		Map<String, Object> productMap = new HashMap<>();
		productMap.put("content", productList);
		productMap.put("currentPage", products.getNumber());
		productMap.put("totalElements", products.getTotalElements());
		productMap.put("totalPages", products.getTotalPages());

		return productMap;
	}

	@Override
	public void saveFromExcelToDb(MultipartFile file, String sheetNumber) {
		try{
			List<Product> products = ProductExcelHelper.excelToProducts(file.getInputStream(), sheetNumber);
			productRepository.saveAll(products);
		} catch (IOException e) {
			throw new RuntimeException("fail to store excel data: " + e.getMessage());
		}
	}

	@Override
	public void saveFromCsvToDb(MultipartFile file) {
		try{
			List<Product> products = ProductExcelHelper.csvToProduct(file.getInputStream());
			productRepository.saveAll(products);
		} catch (IOException e) {
			throw new RuntimeException("fail to store csv data: " + e.getMessage());
		}
	}

	@Override
	public ByteArrayInputStream loadFromDbToExcel() {
		List<Product> products = productRepository.findAll();
		ByteArrayInputStream in = ProductExcelHelper.productsToExcel(products);
		return in;
	}

	@Override
	public ByteArrayInputStream loadFromDbToCsv() {
		List<Product> products = productRepository.findAll();
		ByteArrayInputStream in = ProductExcelHelper.productsToCsv(products);
		return in;
	}

}
