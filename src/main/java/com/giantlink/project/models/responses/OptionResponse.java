package com.giantlink.project.models.responses;

import com.giantlink.glintranetdto.models.responses.ProjectResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionResponse {

	private Long id;
	private String optionName;
	private ProjectResponse project;

}
