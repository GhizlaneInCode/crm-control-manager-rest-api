package com.giantlink.project.models.responses;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponse {

	private Long id;
	private String teamName;
	private Boolean status;
	private Set<UserResponse> team_users;
	private ProjectResponse project;
}
