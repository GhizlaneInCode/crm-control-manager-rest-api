package com.giantlink.project.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.giantlink.project.entities.Option;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
	
	Optional<Option> findByOptionName(String optionName);
	
	Page<Option> findByOptionNameContainingIgnoreCase(String name, Pageable pageable);

}
