package com.giantlink.project.mappers;

import java.util.List;
import java.util.Set;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.giantlink.project.entities.Client;
import com.giantlink.project.models.requests.ClientRequest;
import com.giantlink.project.models.responses.ClientResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClientMapper {

	ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

	List<ClientResponse> mapClient(List<Client> clients);

	Set<ClientResponse> mapClient(Set<Client> clients);

	Client requestToEntity(ClientRequest clientRequest);

	ClientResponse entityToResponse(Client client);
}
