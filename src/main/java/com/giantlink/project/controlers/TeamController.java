package com.giantlink.project.controlers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.models.requests.TeamRequest;
import com.giantlink.project.models.responses.TeamResponse;
import com.giantlink.project.services.TeamService;

@RestController
@RequestMapping("/api/team")
@CrossOrigin(origins = { "http://localhost:4200" })
public class TeamController {

	@Autowired
	TeamService teamService;

	@GetMapping("/all")
	public ResponseEntity<List<TeamResponse>> getTeams() {
		return new ResponseEntity<List<TeamResponse>>(teamService.getTeams(), HttpStatus.OK);
	}

	@PostMapping()
	public ResponseEntity<TeamResponse> addTeam(@RequestBody TeamRequest TeamRequest)
			throws GlAlreadyExistException, GlNotFoundException {
		return new ResponseEntity<TeamResponse>(teamService.addTeam(TeamRequest), HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<TeamResponse> getTeam(@PathVariable Long id) throws GlNotFoundException {
		return new ResponseEntity<TeamResponse>(teamService.getTeam(id), HttpStatus.OK);
	}

	@PutMapping("/status/{id}")
	public ResponseEntity<String> changeStatus(@PathVariable("id") Long id, @RequestParam Boolean status)
			throws GlNotFoundException {
		teamService.changeStatus(id, status);
		return new ResponseEntity<String>("status changed!", HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") Long id) throws GlNotFoundException {
		teamService.deleteTeam(id);
		return new ResponseEntity<String>("deleted !", HttpStatus.OK);
	}

	@PutMapping("/{id}")
	public ResponseEntity<TeamResponse> update(@PathVariable("id") Long id, @RequestBody TeamRequest TeamRequest)
			throws GlNotFoundException {
		return new ResponseEntity<TeamResponse>(teamService.updateTeam(id, TeamRequest), HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<Map<String, Object>> getAll(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "2") int size, @RequestParam(defaultValue = "", name = "name") String name) {
		Pageable pageable = PageRequest.of(page, size);
		return new ResponseEntity<Map<String, Object>>(teamService.getAllPaginations(name, pageable), HttpStatus.OK);
	}

}
