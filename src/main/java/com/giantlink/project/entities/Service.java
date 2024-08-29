package com.giantlink.project.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "services")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String serviceName;
	private Float point;
	private Boolean statut;
	
	@ManyToMany(mappedBy = "services")
	@JsonBackReference
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Set<Lead> leads;

	@ManyToOne()
	@JoinColumn(name = "serviceType_id", nullable = false)
	@JsonBackReference
	@OnDelete(action=OnDeleteAction.CASCADE)
	private ServiceType serviceType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date timestamp;

	@PrePersist
	private void onCreate() {
		this.timestamp = new Date();
	}
	

}
