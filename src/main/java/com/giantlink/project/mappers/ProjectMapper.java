package com.giantlink.project.mappers;

import java.util.List;
import java.util.Set;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.giantlink.project.entities.Project;
import com.giantlink.project.models.requests.ProjectRequest;
import com.giantlink.project.models.responses.ProjectResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProjectMapper {

	ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

	// @Mapping(target = "teams", ignore = true)
	ProjectResponse mapEntity(Project entity);

	Project mapRequest(ProjectRequest entity);

	Project mapResponse(ProjectResponse entity);

	Set<ProjectResponse> mapResponses(Set<Project> entities);

	List<ProjectResponse> mapResponses(List<Project> findAll);
}
