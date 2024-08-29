package com.giantlink.project.models.responses;

import com.giantlink.glintranetdto.models.responses.ProjectResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PackResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String packName;
	private ProjectResponse project;
}
