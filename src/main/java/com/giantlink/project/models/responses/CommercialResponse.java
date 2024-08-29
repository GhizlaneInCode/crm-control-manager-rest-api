package com.giantlink.project.models.responses;

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
public class CommercialResponse {

	private Long id;

	private String commercialName;
	private Boolean statut;
	//private Long idCalendar;

	// private Set<LeadResponse> leads;
}
