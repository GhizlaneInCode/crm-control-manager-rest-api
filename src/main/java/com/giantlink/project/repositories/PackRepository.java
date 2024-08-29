package com.giantlink.project.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.giantlink.project.entities.Pack;

@Repository
public interface PackRepository extends JpaRepository<Pack, Long> {

	Optional<Pack> findByPackName(String packName);

	Page<Pack> findByPackNameContainingIgnoreCase(String packName, Pageable pageable);
}