package com.giantlink.project.entities;

import javax.persistence.*;

import com.giantlink.glintranetdto.models.responses.ProjectResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Entity
@Table(indexes = @Index(columnList = "projectId"), name="packs")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pack implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String packName;

	private Long projectId;

    public ProjectResponse getProject() {
		return ProjectResponse.builder()
				.id(projectId)
				.build();
    }
}
