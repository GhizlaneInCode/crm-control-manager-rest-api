package com.giantlink.project.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.giantlink.project.entities.Project;
import com.giantlink.project.entities.Team;
import com.giantlink.project.entities.User;
import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.mappers.TeamMapper;
import com.giantlink.project.models.requests.TeamRequest;
import com.giantlink.project.models.responses.TeamResponse;
import com.giantlink.project.repositories.ProjectRepository;
import com.giantlink.project.repositories.TeamRepository;
import com.giantlink.project.repositories.UserRepository;
import com.giantlink.project.services.TeamService;

@Service
public class TeamServiceImpl implements TeamService {

	@Autowired
	TeamRepository teamRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ProjectRepository projectRepository;

	@Override
	public TeamResponse addTeam(TeamRequest teamRequest) throws GlNotFoundException, GlAlreadyExistException {
		Optional<Team> teamSearch = teamRepository.findByTeamName(teamRequest.getTeamName());
		if (teamSearch.isPresent()) {
			throw new GlAlreadyExistException(teamRequest.getTeamName(), Team.class.getSimpleName());
		}

		Optional<Project> projectSearch = projectRepository.findById(teamRequest.getProjectId());
		if (projectSearch.isEmpty()) {
			throw new GlNotFoundException("project", Project.class.getSimpleName());
		}

		Team team = TeamMapper.INSTANCE.mapRequest(teamRequest);
		team.setStatus(true);
		team.setProject(projectSearch.get());

		return TeamMapper.INSTANCE.mapEntity(teamRepository.save(team));
	}

	@Override
	public void deleteTeam(Long id) throws GlNotFoundException {
		Optional<Team> teamSearch = teamRepository.findById(id);
		if (teamSearch.isEmpty()) {
			throw new GlNotFoundException("team", Team.class.getSimpleName());
		} else {
			for (User user : teamSearch.get().getTeam_users()) {
				if (user.getTeams().size() <= 1) {
					if (!user.getTeams().contains(teamRepository.findByTeamName("default_team").get())) {
						user.getTeams().add(teamRepository.findByTeamName("default_team").get());
						user.getTeams().remove(teamSearch.get());
						userRepository.save(user);
					}
				}
			}
			teamRepository.delete(teamSearch.get());
		}
	}

	@Override
	public TeamResponse updateTeam(Long id, TeamRequest teamRequest) throws GlNotFoundException {
		Optional<Team> teamSearch = teamRepository.findById(id);
		if (teamSearch.isEmpty()) {
			throw new GlNotFoundException("team", Team.class.getSimpleName());
		}
		Set<User> users = new HashSet<>();
		
		Optional<Project> projectSearch = projectRepository.findById(teamRequest.getProjectId());
		teamSearch.get().setProject(projectSearch.get());
		teamSearch.get().setStatus(teamRequest.getStatus());
		teamSearch.get().setTeam_users(users);
		teamSearch.get().setTeamName(teamRequest.getTeamName());
		return TeamMapper.INSTANCE.mapEntity(teamRepository.save(teamSearch.get()));
	}

	@Override
	public TeamResponse getTeam(Long id) throws GlNotFoundException {
		Optional<Team> teamSearch = teamRepository.findById(id);
		if (teamSearch.isEmpty()) {
			throw new GlNotFoundException("team", Team.class.getSimpleName());
		} else {
			return TeamMapper.INSTANCE.mapEntity(teamSearch.get());
		}
	}

	@Override
	public List<TeamResponse> getTeams() {
		return TeamMapper.INSTANCE.mapResponses(teamRepository.findAll());
	}

	@Override
	public Map<String, Object> getAllPaginations(String name, Pageable pageable) {
		List<TeamResponse> teamResponses = new ArrayList<>();
		Page<Team> teams = (name.isBlank()) ? teamRepository.findAll(pageable)
				: teamRepository.findByTeamName(name, pageable);

		teams.getContent().forEach(team -> {
			teamResponses.add(TeamMapper.INSTANCE.mapEntity(team));
		});

		Map<String, Object> requestResponse = new HashMap<>();
		requestResponse.put("content", teamResponses);
		requestResponse.put("currentPage", teams.getNumber());
		requestResponse.put("totalElements", teams.getTotalElements());
		requestResponse.put("totalPages", teams.getTotalPages());
		return requestResponse;
	}

	@Override
	public void changeStatus(Long id, Boolean status) throws GlNotFoundException {
		Optional<Team> team = teamRepository.findById(id);
		if (team.isEmpty()) {
			throw new GlNotFoundException("team", Team.class.getSimpleName());
		}
		team.get().setStatus(status);
		teamRepository.save(team.get());
	}


}
