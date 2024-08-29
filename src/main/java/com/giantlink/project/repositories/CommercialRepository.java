package com.giantlink.project.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.giantlink.project.entities.Commercial;

@Repository
public interface CommercialRepository extends JpaRepository<Commercial, Long> {
	
	Optional<Commercial> findByCommercialName(String commercialName);
	
	Page<Commercial> findByCommercialNameContainingIgnoreCase(String name, Pageable pageable);

}
