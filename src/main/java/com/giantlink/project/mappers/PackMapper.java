package com.giantlink.project.mappers;

import java.util.List;
import java.util.Set;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.giantlink.project.entities.Pack;
import com.giantlink.project.models.requests.PackRequest;
import com.giantlink.project.models.responses.PackResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PackMapper {

	PackMapper INSTANCE = Mappers.getMapper(PackMapper.class);

	List<PackResponse> mapPack(List<Pack> Packs);

	Set<PackResponse> mapPack(Set<Pack> Packs);

	Pack requestToEntity(PackRequest PackRequest);

	PackResponse entityToResponse(Pack Pack);
}
