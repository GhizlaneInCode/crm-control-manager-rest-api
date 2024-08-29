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
public class ClientResponse {

	private Long id;

	private String firstName;
	private String lastName;
	private String adress;
	private String tele;
	private String email;
	private String gsm;
	private String cp;
	private String city;

	// private Set<LeadResponse> leads;

}
