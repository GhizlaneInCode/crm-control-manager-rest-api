package com.giantlink.project.models.requests;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {
	
	private String firstName;
	private String lastName;
	private String adress;
	private String tele;
	private String email;
	private String gsm;
	private String cp;
	private String city;

}
