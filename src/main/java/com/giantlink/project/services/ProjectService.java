package com.giantlink.project.services;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.giantlink.project.entities.Project;
import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.models.requests.ProjectRequest;
import com.giantlink.project.models.responses.ProjectResponse;

public interface ProjectService {

	ProjectResponse addProject(ProjectRequest projectRequest) throws GlNotFoundException, GlAlreadyExistException;

	void deleteProject(Long id) throws GlNotFoundException;

	void changeStatus(Long id,Boolean status) throws GlNotFoundException;
	
	ProjectResponse updateProject(Long id, ProjectRequest projectRequest) throws GlNotFoundException;

	ProjectResponse getProject(Long id) throws GlNotFoundException;

	List<Project> getProjects();
	
	List<ProjectResponse> getProjectsResponses();

	Map<String, Object> getAllPaginations(String name,Pageable pageable);
}
