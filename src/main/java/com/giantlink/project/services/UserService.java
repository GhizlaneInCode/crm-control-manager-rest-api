package com.giantlink.project.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.models.requests.UserRequest;
import com.giantlink.project.models.responses.UserResponse;

public interface UserService {

	UserResponse addUser(UserRequest userRequest) throws GlAlreadyExistException, GlNotFoundException;

	void deleteUser(Long id) throws GlNotFoundException;

	UserResponse updateUser(Long id, UserRequest userRequest) throws GlNotFoundException;

	UserResponse getUser(Long id) throws GlNotFoundException;

	UserResponse getUser(String userName) throws GlNotFoundException;

	List<UserResponse> getUsers();

	Map<String, Object> getAllPaginations(String name, Pageable pageable);

	void changeRole(Long userId, Long roleId) throws GlNotFoundException;

	void refreshToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException;
}
