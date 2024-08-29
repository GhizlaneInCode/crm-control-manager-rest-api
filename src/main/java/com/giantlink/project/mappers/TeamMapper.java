package com.giantlink.project.mappers;

import java.util.List;
import java.util.Set;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.giantlink.project.entities.Team;
import com.giantlink.project.models.requests.TeamRequest;
import com.giantlink.project.models.responses.TeamResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TeamMapper {

	TeamMapper INSTANCE = Mappers.getMapper(TeamMapper.class);

	// @Mapping(target = "project", ignore = true)
	TeamResponse mapEntity(Team entity);

	Team mapRequest(TeamRequest entity);

	Team mapResponse(TeamResponse entity);

	Set<TeamResponse> mapResponses(Set<Team> entities);

	List<TeamResponse> mapResponses(List<Team> findAll);
}
