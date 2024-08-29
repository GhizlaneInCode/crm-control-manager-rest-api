package com.giantlink.project.models.requests;

import java.util.Date;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadRequest {

	//private Long userId;
	private Long employeeId;
	private Long commercialId;
	private ClientRequest client;
	private Long productId;
	private Long serviceTypeId;
	private Set<Long> serviceIds;

	private Date appointmentDate;

	private Date appointmentTime;
	
	private String voice;
	private String callType;
	private String soldGsm;
	
	private Boolean coachValidation;
	private String addedOption;
	private String deletedOption;
	private Long ecouteId;
	private float price;
	private Long amount;
	private Boolean encode;
	private String comment;
	private Date leadDate;
	
	
	
	

}
