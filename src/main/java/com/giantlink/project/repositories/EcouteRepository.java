package com.giantlink.project.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.giantlink.project.entities.EEcoute;
import com.giantlink.project.entities.Ecoute;

public interface EcouteRepository extends JpaRepository<Ecoute, Long> {
	
	Optional<Ecoute> findByName(EEcoute name);

}
