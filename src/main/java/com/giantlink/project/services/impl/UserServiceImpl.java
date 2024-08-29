package com.giantlink.project.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.giantlink.project.entities.Role;
import com.giantlink.project.entities.Team;
import com.giantlink.project.entities.User;
import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;
import com.giantlink.project.mappers.UserMapper;
import com.giantlink.project.models.requests.UserRequest;
import com.giantlink.project.models.responses.UserResponse;
import com.giantlink.project.repositories.LeadRepository;
import com.giantlink.project.repositories.RoleRepository;
import com.giantlink.project.repositories.TeamRepository;
import com.giantlink.project.repositories.UserRepository;
import com.giantlink.project.services.UserService;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	TeamRepository teamRepository;

	@Autowired
	LeadRepository leadRepository;

	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Override
	public UserResponse addUser(UserRequest userRequest) throws GlAlreadyExistException, GlNotFoundException {
		Optional<User> userSearch = userRepository.findByUserName(userRequest.getUserName());
		if (userSearch.isPresent()) {
			throw new GlAlreadyExistException(userRequest.getUserName(), User.class.getSimpleName());
		} else {
			Optional<Role> role = roleRepository.findById(userRequest.getIdRole());
			if (role.isEmpty()) {
				throw new GlNotFoundException("role", Role.class.getSimpleName());
			}
			Set<Team> teams = new HashSet<>();
			for (Long idTeam : userRequest.getIdTeams()) {
				Optional<Team> team = teamRepository.findById(idTeam);
				if (team.isEmpty()) {
					throw new GlNotFoundException("team", Team.class.getSimpleName());
				}
				teams.add(team.get());
			}
			User us = UserMapper.INSTANCE.mapRequest(userRequest);

			us.setPassword(encoder.encode(userRequest.getPassword()));
			us.setTeams(teams);
			us.setRole(role.get());

			return UserMapper.INSTANCE.mapEntity(userRepository.save(us));
		}
	}

	@Override
	public void deleteUser(Long id) throws GlNotFoundException {
		Optional<User> userSearch = userRepository.findById(id);
		if (userSearch.isEmpty()) {
			throw new GlNotFoundException("user", User.class.getSimpleName());
		} else {
			userRepository.delete(userSearch.get());
		}
	}

	@Override
	public UserResponse updateUser(Long id, UserRequest userRequest) throws GlNotFoundException {
		Optional<User> userSearch = userRepository.findById(id);
		if (userSearch.isEmpty()) {
			throw new GlNotFoundException("user", User.class.getSimpleName());
		} else {

			Optional<Role> role = roleRepository.findById(userRequest.getIdRole());
			if (role.isEmpty()) {
				throw new GlNotFoundException("role", Role.class.getSimpleName());
			}
			Set<Team> teams = new HashSet<>();
			for (Long idTeam : userRequest.getIdTeams()) {
				Optional<Team> team = teamRepository.findById(idTeam);
				if (team.isEmpty()) {
					throw new GlNotFoundException("team", Team.class.getSimpleName());
				}
				teams.add(team.get());
			}
			if (teams.size() > 0) {
				userSearch.get().getTeams().remove(teamRepository.findByTeamName("default_team").get());
			}
			userSearch.get().setFirstName(userRequest.getFirstName());
			userSearch.get().setLastName(userRequest.getLastName());
			userSearch.get().setPassword(encoder.encode(userRequest.getPassword()));
			userSearch.get().setUserName(userRequest.getUserName());
			userSearch.get().setLanguage(userRequest.getLanguage());
			userSearch.get().setRole(role.get());
			userSearch.get().setTeams(teams);

			return UserMapper.INSTANCE.mapEntity(userRepository.save(userSearch.get()));
		}
	}

	@Override
	public UserResponse getUser(Long id) throws GlNotFoundException {
		Optional<User> userSearch = userRepository.findById(id);
		if (userSearch.isEmpty()) {
			throw new GlNotFoundException("user", User.class.getSimpleName());
		} else {
			return UserMapper.INSTANCE.mapEntity(userSearch.get());
		}
	}

	@Override
	public UserResponse getUser(String userName) throws GlNotFoundException {
		Optional<User> userSearch = userRepository.findByUserName(userName);
		if (userSearch.isEmpty()) {
			throw new GlNotFoundException(userName, User.class.getSimpleName());
		} else {
			return UserMapper.INSTANCE.mapEntity(userSearch.get());
		}
	}

	@Override
	public List<UserResponse> getUsers() {
		return UserMapper.INSTANCE.mapResponses(userRepository.findAll());
	}

	@Override
	public void changeRole(Long userId, Long roleId) throws GlNotFoundException {
		Optional<User> userSearch = userRepository.findById(userId);
		if (userSearch.isEmpty()) {
			throw new GlNotFoundException("user", User.class.getSimpleName());
		} else {
			Optional<Role> roleSearch = roleRepository.findById(roleId);
			if (roleSearch.isEmpty()) {
				throw new GlNotFoundException("role", Role.class.getSimpleName());
			} else {
				userSearch.get().setRole(roleSearch.get());
				userRepository.save(userSearch.get());
			}
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUserName(username).get();
		if (user == null) {
			throw new UsernameNotFoundException("user : " + username + "not found");
		} else {
			System.out.println("user exist");
		}
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

		authorities.add(new SimpleGrantedAuthority(user.getRole().getName().toString()));

		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
				authorities);
	}

	@Override
	public Map<String, Object> getAllPaginations(String name, Pageable pageable) {
		List<UserResponse> usersResponses = new ArrayList<>();
		Page<User> users = (name.isBlank()) ? userRepository.findAll(pageable)
				: userRepository.findByUserName(name, pageable);

		users.getContent().forEach(user -> {
			usersResponses.add(UserMapper.INSTANCE.mapEntity(user));
		});

		Map<String, Object> requestResponse = new HashMap<>();
		requestResponse.put("content", usersResponses);
		requestResponse.put("currentPage", users.getNumber());
		requestResponse.put("totalElements", users.getTotalElements());
		requestResponse.put("totalPages", users.getTotalPages());
		return requestResponse;
	}

	@Override
	public void refreshToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			try {

				String refresh_token = authorizationHeader.substring("Bearer ".length());
				Algorithm algorithm = Algorithm.HMAC256("GiantLink_Vente".getBytes());
				JWTVerifier jwtVerifier = JWT.require(algorithm).build();
				DecodedJWT decodedJWT = jwtVerifier.verify(refresh_token);
				String username = decodedJWT.getSubject();
				User user = UserMapper.INSTANCE.mapResponse(getUser(username));

				List<GrantedAuthority> authorities = new ArrayList<>();

				authorities.add(new SimpleGrantedAuthority(user.getRole().getName().toString()));

				String access_token = JWT.create().withSubject(user.getUserName())
						.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
						.withIssuer(request.getRequestURL().toString())
						.withClaim("roles",
								authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
						.sign(algorithm);

				Map<String, String> tokens = new HashMap<>();
				tokens.put("access_token", access_token);
				tokens.put("refresh_token", refresh_token);

				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);
			} catch (Exception e) {
				response.setHeader("error", e.getMessage());
				response.setStatus(403);
				Map<String, String> error = new HashMap<>();
				error.put("error_message", e.getMessage());
				response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
		} else {
			throw new RuntimeException("Refresh token not valid");
		}
	}

}
