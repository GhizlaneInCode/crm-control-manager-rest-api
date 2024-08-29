package com.giantlink.project.entities;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "serviceTypes")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceType {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String label;
	
	@OneToMany(mappedBy = "serviceType",fetch=FetchType.EAGER,orphanRemoval = true)
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Set<Service> services;
	

}
