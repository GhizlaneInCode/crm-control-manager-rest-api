package com.giantlink.project.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.giantlink.project.entities.Project;
import com.giantlink.project.entities.Team;
import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.mappers.ProjectMapper;
import com.giantlink.project.models.requests.ProjectRequest;
import com.giantlink.project.models.responses.ProjectResponse;
import com.giantlink.project.repositories.ProjectRepository;
import com.giantlink.project.repositories.TeamRepository;
import com.giantlink.project.services.ProjectService;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	TeamRepository teamRepository;

	@Autowired
	ProjectRepository projectRepository;

	@Override
	public ProjectResponse addProject(ProjectRequest projectRequest)
			throws GlNotFoundException, GlAlreadyExistException {
		Optional<Project> projectSearch = projectRepository.findByProjectName(projectRequest.getProjectName());
		if (projectSearch.isPresent()) {
			throw new GlAlreadyExistException(projectRequest.getProjectName(), Project.class.getSimpleName());
		} else {
			Project project = ProjectMapper.INSTANCE.mapRequest(projectRequest);
			project.setStatus(true);
			return ProjectMapper.INSTANCE.mapEntity(projectRepository.save(project));
		}
	}

	@Override
	public void deleteProject(Long id) throws GlNotFoundException {
		Optional<Project> projectSearch = projectRepository.findById(id);
		Project defaultProject = projectRepository.findByProjectName("default_project").get();
		if (projectSearch.isEmpty()) {
			throw new GlNotFoundException("project", Project.class.getSimpleName());
		}

		if (!projectSearch.get().getTeams().isEmpty()) {
			for (Team team : projectSearch.get().getTeams()) {
				System.out.println(team.getTeamName());
				team.setProject(defaultProject);
				teamRepository.save(team);
			}
		}
		projectRepository.delete(projectSearch.get());
	}

	@Override
	public ProjectResponse updateProject(Long id, ProjectRequest projectRequest) throws GlNotFoundException {
		Optional<Project> projectSearch = projectRepository.findById(id);

		if (projectSearch.isEmpty()) {
			throw new GlNotFoundException("project", Project.class.getSimpleName());
		}
		projectSearch.get().setProjectName(projectRequest.getProjectName());
		projectSearch.get().setProjectType(projectRequest.getProjectType());
		projectSearch.get().setStatus(projectRequest.getStatus());
		projectSearch.get().setStartDate(projectRequest.getStartDate());
		projectSearch.get().setFinishDate(projectRequest.getFinishDate());
		return ProjectMapper.INSTANCE.mapEntity(projectRepository.save(projectSearch.get()));
	}

	@Override
	public ProjectResponse getProject(Long id) throws GlNotFoundException {
		Optional<Project> projectSearch = projectRepository.findById(id);
		if (projectSearch.isEmpty()) {
			throw new GlNotFoundException("project", Project.class.getSimpleName());
		} else {
			return ProjectMapper.INSTANCE.mapEntity(projectSearch.get());
		}
	}

	@Override
	public List<Project> getProjects() {
		return projectRepository.findAll();
	}

	@Override
	public Map<String, Object> getAllPaginations(String name, Pageable pageable) {
		List<ProjectResponse> projectResponses = new ArrayList<>();
		Page<Project> projects = (name.isBlank()) ? projectRepository.findAll(pageable)
				: projectRepository.findByProjectNameContainingIgnoreCase(name, pageable);

		projects.getContent().forEach(project -> {
			projectResponses.add(ProjectMapper.INSTANCE.mapEntity(project));
		});

		Map<String, Object> requestResponse = new HashMap<>();
		requestResponse.put("content", projectResponses);
		requestResponse.put("currentPage", projects.getNumber());
		requestResponse.put("totalElements", projects.getTotalElements());
		requestResponse.put("totalPages", projects.getTotalPages());
		return requestResponse;
	}

	@Transactional
	@Override
	public List<ProjectResponse> getProjectsResponses() {
		return ProjectMapper.INSTANCE.mapResponses(projectRepository.findAll());
	}

	@Override
	public void changeStatus(Long id, Boolean status) throws GlNotFoundException {
		Optional<Project> project = projectRepository.findById(id);
		if (project.isEmpty()) {
			throw new GlNotFoundException("project", Project.class.getSimpleName());
		}
		project.get().setStatus(status);
		projectRepository.save(project.get());
	}

}
