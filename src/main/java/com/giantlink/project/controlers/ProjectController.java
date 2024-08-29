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

import com.giantlink.project.entities.Project;
import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.models.requests.ProjectRequest;
import com.giantlink.project.models.responses.ProjectResponse;
import com.giantlink.project.services.ProjectService;

@RestController
@RequestMapping("/api/project")
@CrossOrigin(origins = { "http://localhost:4200" })
public class ProjectController {
	@Autowired
	ProjectService projectService;

	@GetMapping("/all")
	public ResponseEntity<List<Project>> getprojects() {
		return new ResponseEntity<List<Project>>(projectService.getProjects(), HttpStatus.OK);
	}

	@PostMapping()
	public ResponseEntity<ProjectResponse> addproject(@RequestBody ProjectRequest projectRequest)
			throws GlAlreadyExistException, GlNotFoundException {
		return new ResponseEntity<ProjectResponse>(projectService.addProject(projectRequest), HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProjectResponse> getproject(@PathVariable Long id) throws GlNotFoundException {
		return new ResponseEntity<ProjectResponse>(projectService.getProject(id), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") Long id) throws GlNotFoundException {
		projectService.deleteProject(id);
		return new ResponseEntity<String>("deleted !", HttpStatus.OK);
	}

	@PutMapping("/status/{id}")
	public ResponseEntity<String> changeStatus(@PathVariable("id") Long id, @RequestBody Boolean status)
			throws GlNotFoundException {
		projectService.changeStatus(id, status);
		return new ResponseEntity<String>("status changed!", HttpStatus.OK);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProjectResponse> update(@PathVariable("id") Long id,
			@RequestBody ProjectRequest projectRequest) throws GlNotFoundException {
		return new ResponseEntity<ProjectResponse>(projectService.updateProject(id, projectRequest), HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<Map<String, Object>> getAll(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, @RequestParam(defaultValue = "", name = "name") String name) {
		Pageable pageable = PageRequest.of(page, size);
		return new ResponseEntity<Map<String, Object>>(projectService.getAllPaginations(name, pageable), HttpStatus.OK);
	}
}
