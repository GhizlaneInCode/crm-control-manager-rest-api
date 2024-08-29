package com.giantlink.project.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.giantlink.project.entities.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

	Optional<Team> findByTeamName(String teamName);

	Page<Team> findByTeamName(String teamName, Pageable pageable);
}
