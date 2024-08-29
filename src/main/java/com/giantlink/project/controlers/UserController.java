package com.giantlink.project.controlers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import com.giantlink.project.models.requests.UserRequest;
import com.giantlink.project.models.responses.UserResponse;
import com.giantlink.project.services.UserService;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = { "http://localhost:4200" })
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping("/all")
	public ResponseEntity<List<UserResponse>> getUsers() {
		return new ResponseEntity<List<UserResponse>>(userService.getUsers(), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<UserResponse> add(@RequestBody UserRequest userRequest)
			throws GlAlreadyExistException, GlNotFoundException {
		return new ResponseEntity<UserResponse>(userService.addUser(userRequest), HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUser(@PathVariable Long id) throws GlNotFoundException {
		return new ResponseEntity<UserResponse>(userService.getUser(id), HttpStatus.OK);
	}

	@GetMapping("/name")
	public ResponseEntity<UserResponse> getUserByUserName(@RequestParam String userName) throws GlNotFoundException {
		return new ResponseEntity<UserResponse>(userService.getUser(userName), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") Long id) throws GlNotFoundException {
		userService.deleteUser(id);
		return new ResponseEntity<String>("deleted !", HttpStatus.OK);
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserResponse> update(@PathVariable("id") Long id, @RequestBody UserRequest userRequest)
			throws GlNotFoundException {
		return new ResponseEntity<UserResponse>(userService.updateUser(id, userRequest), HttpStatus.OK);
	}

	@PutMapping("/role/{id}/{idRole}")
	public ResponseEntity<String> grantRole(@PathVariable("id") Long id, @PathVariable("idRole") Long idRole)
			throws GlNotFoundException {
		userService.changeRole(id, idRole);
		return new ResponseEntity<String>("Role Granted !", HttpStatus.ACCEPTED);
	}

	@GetMapping
	// @PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<Map<String, Object>> getAll(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "2") int size, @RequestParam(defaultValue = "", name = "name") String name) {
		Pageable pageable = PageRequest.of(page, size);
		return new ResponseEntity<Map<String, Object>>(userService.getAllPaginations(name, pageable), HttpStatus.OK);
	}

	@GetMapping("/refreshtoken")
	public void refreashToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException {
		userService.refreshToken(request, response, authentication);
	}

}
