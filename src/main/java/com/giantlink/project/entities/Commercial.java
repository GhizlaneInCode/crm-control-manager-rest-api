package com.giantlink.project.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "commercials")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commercial {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String commercialName;
	private Boolean statut;
	// private Long idCalendar;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date timestamp;

	@OneToMany(mappedBy = "commercial", fetch = FetchType.EAGER)
	@OnDelete(action=OnDeleteAction.CASCADE)
	private Set<Lead> leads;

	@PrePersist
	private void onCreate() {
		this.timestamp = new Date();
	}

}
