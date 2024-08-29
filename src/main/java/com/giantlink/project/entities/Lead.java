package com.giantlink.project.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.AttributeOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
//@Table(name = "leads")
@Table(indexes = @Index(columnList = "employeeId"), name="leads")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private float totalPoint;
	private String voice;
	private String callType;
	private Boolean coachValidation;
	
	@Size(max= 500)
	private String addedOption;
	
	@Size(max= 500)
	private String deletedOption;
	private float price;
	private Long amount;
	private Boolean encode;
	private String comment;
	
	@Size(max= 500)
	private String soldGsm;

	
	private Long serviceTypeId;
	
	@Temporal(TemporalType.DATE)
	private Date leadDate;
	
	
	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "ecoute_id", nullable = false)
	private Ecoute ecoute;
	
	
	@Temporal(TemporalType.DATE)
	private Date appointmentDate;

	@Basic
	@Temporal(TemporalType.TIME)
	private Date appointmentTime;

	//@ManyToOne(cascade = CascadeType.PERSIST)
	//@JoinColumn(name = "user_id", nullable = false)
	//@JsonBackReference
	//private User user;
	
	private Long employeeId;

	@ManyToOne()
	@JoinColumn(name = "commercial_id", nullable = false)
	@JsonBackReference
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Commercial commercial;
	
	@ManyToOne()
	@JoinColumn(name = "product_id", nullable = false)
	@JsonBackReference
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Product product;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "Leads_Services",
			joinColumns = @JoinColumn(name = "lead_id"),
			inverseJoinColumns = @JoinColumn(name = "service_id")
			)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Set<Service> services;

	@ManyToOne()
	@JoinColumn(name = "client_id", nullable = false)
	@JsonBackReference
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Client client;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date timestamp;

	@PrePersist
	private void onCreate() {
		this.timestamp = new Date();
	}
	
	public Lead(Long id, Long employeeId, double totalPoint) {
		this.id = id;
		this.employeeId = employeeId;
		this.totalPoint = (float)totalPoint;
	}
	


}
