package com.giantlink.project.models.responses;

import com.giantlink.project.entities.ServiceType;
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
public class ServiceResponse {

	private Long id;

	private String serviceName;
	private Float point;
	private Boolean statut;
	private ServiceType serviceType;
	
	private TopService topService;

	// private Set<LeadResponse> leads;
}
