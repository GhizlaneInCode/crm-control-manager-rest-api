package com.giantlink.project.services;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.models.requests.TeamRequest;
import com.giantlink.project.models.responses.TeamResponse;

public interface TeamService {

	TeamResponse addTeam(TeamRequest teamRequest) throws GlAlreadyExistException, GlNotFoundException;

	void deleteTeam(Long id) throws GlNotFoundException;
	
	void changeStatus(Long id,Boolean status) throws GlNotFoundException;

	TeamResponse updateTeam(Long id, TeamRequest teamRequest) throws GlNotFoundException;

	TeamResponse getTeam(Long id) throws GlNotFoundException;

	List<TeamResponse> getTeams();

	Map<String, Object> getAllPaginations(String name,Pageable pageable);
}
