package com.giantlink.project.services.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.giantlink.project.helpers.ClientExcelHelper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.giantlink.project.entities.Client;
import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.mappers.ClientMapper;
import com.giantlink.project.models.requests.ClientRequest;
import com.giantlink.project.models.responses.ClientResponse;
import com.giantlink.project.repositories.ClientRepository;
import com.giantlink.project.services.ClientService;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService  {
	
	@Autowired
	private ClientRepository clientRepository;
	@Override
	public ClientResponse add(ClientRequest clientRequest) throws GlAlreadyExistException, GlNotFoundException {

		
		Optional<Client> findClient = clientRepository.findByLastName(clientRequest.getLastName());

		if (findClient.isPresent()) {
			throw new GlAlreadyExistException(clientRequest.getLastName(), Client.class.getSimpleName());
		}
		return ClientMapper.INSTANCE
				.entityToResponse(clientRepository.save(ClientMapper.INSTANCE.requestToEntity(clientRequest)));


	}
	
	


	@Override
	public List<ClientResponse> getAll() {
		return ClientMapper.INSTANCE.mapClient(clientRepository.findAll());
	}

	@Override
	public ClientResponse get(Long id) throws GlNotFoundException {
		Optional<Client> findClient = clientRepository.findById(id);

		if (!findClient.isPresent()) {
			throw new GlNotFoundException(id.toString(), Client.class.getSimpleName());
		}

		return ClientMapper.INSTANCE.entityToResponse(clientRepository.findById(id).get());
		
	}

	@Override
	public void delete(Long id) throws GlNotFoundException {
		Optional<Client> findClient = clientRepository.findById(id);

		if (!findClient.isPresent()) {
			throw new GlNotFoundException(id.toString(), Client.class.getSimpleName());
		} 
		
		clientRepository.deleteById(id);
		
	}

	@Override
	public ClientResponse update(Long id, ClientRequest clientRequest) throws GlNotFoundException {
		
		Optional<Client> findClient = clientRepository.findById(id);

		if (!findClient.isPresent()) {
			throw new GlNotFoundException(id.toString(), Client.class.getSimpleName());
		}
		
		Client client = clientRepository.findById(id).get();

		client.setFirstName(clientRequest.getFirstName());
		client.setLastName(clientRequest.getLastName());
		client.setEmail(clientRequest.getEmail());
		client.setAdress(clientRequest.getAdress());
		client.setCity(clientRequest.getCity());
		client.setCp(clientRequest.getCp());
		client.setGsm(clientRequest.getGsm());
		client.setTele(clientRequest.getTele());

		clientRepository.save(client);

		return ClientMapper.INSTANCE.entityToResponse(client);
		
	}

	@Override
	public Map<String, Object> getAllPaginations(String name, Pageable pageable) {
		
		List<ClientResponse> clientList = new ArrayList<>();
		Page<Client> clients = (name.isBlank()) ? clientRepository.findAll(pageable)
				: clientRepository.findByLastNameContainingIgnoreCase(name, pageable);
		clients.getContent().forEach(client -> {
			ClientResponse response = ClientResponse.builder().id(client.getId())
					.firstName(client.getFirstName())
					.lastName(client.getLastName())
					.email(client.getEmail())
					.adress(client.getAdress())
					.tele(client.getTele())
					.gsm(client.getGsm())
				    .city(client.getCity())
					.cp(client.getCp())
					.build();
			
			clientList.add(response);
		});
		Map<String, Object> clientMap = new HashMap<>();
		clientMap.put("content", clientList);
		clientMap.put("currentPage", clients.getNumber());
		clientMap.put("totalElements", clients.getTotalElements());
		clientMap.put("totalPages", clients.getTotalPages());

		return clientMap;
	}

	@Override
	public void saveFromExcelToDb(MultipartFile file, String sheetNumber) {
		try {
			List<Client> clients = ClientExcelHelper.excelToClients(file.getInputStream() ,sheetNumber);
			clientRepository.saveAll(clients);
		} catch (IOException e) {
			throw new RuntimeException("fail to store excel data: " + e.getMessage());
		}
	}

	@Override
	public void saveFromCsvToDb(MultipartFile file) {
		try {
			List<Client> clients = ClientExcelHelper.csvToClients(file.getInputStream());
			clientRepository.saveAll(clients);
		} catch (IOException e) {
			throw new RuntimeException("fail to store csv data: " + e.getMessage());
		}
	}

	@Override
	public ByteArrayInputStream loadFromDbToExcel() {
		List<Client> clients = clientRepository.findAll();
		ByteArrayInputStream in = ClientExcelHelper.clientsToExcel(clients);
		return in;
	}

	@Override
	public ByteArrayInputStream loadFromDbToCsv() {
		List<Client> clients = clientRepository.findAll();
		ByteArrayInputStream in = ClientExcelHelper.clientsToCsv(clients);
		return in;
	}


}
