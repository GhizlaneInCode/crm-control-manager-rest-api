
package com.giantlink.project;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.giantlink.project.entities.EEcoute;
import com.giantlink.project.entities.ERole;
import com.giantlink.project.entities.Ecoute;
import com.giantlink.project.entities.Project;
import com.giantlink.project.entities.Role;
import com.giantlink.project.entities.Team;
import com.giantlink.project.entities.User;
import com.giantlink.project.repositories.EcouteRepository;
import com.giantlink.project.repositories.ProjectRepository;
import com.giantlink.project.repositories.RoleRepository;
import com.giantlink.project.repositories.TeamRepository;
import com.giantlink.project.repositories.UserRepository;

@SpringBootApplication
public class GlVenteApplication implements CommandLineRunner {

	@Autowired
	UserRepository userRepository;

	@Autowired
	ProjectRepository projectRepository;

	@Autowired
	TeamRepository teamRepository;

	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	EcouteRepository ecouteRepository;

	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	public static void main(String[] args) {
		SpringApplication.run(GlVenteApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		// insert default project
		Project pr = Project.builder().projectName("default_project").projectType("default").startDate(new Date())
				.finishDate(new Date()).status(true).build();
		if (projectRepository.findByProjectName("default_project").isEmpty()) {
			projectRepository.save(pr);
		}

		// insert default team
		Team tm = Team.builder().teamName("default_team").status(true).project(pr).build();
		if (teamRepository.findByTeamName("default_team").isEmpty()) {
			teamRepository.save(tm);
		}

		// insert roles
		for (ERole erole : ERole.values()) {
			Optional<Role> findRole = roleRepository.findByName(erole);
			if (findRole.isEmpty()) {
				Role role = Role.builder().name(erole).build();
				roleRepository.save(role);
			}
		}
		

		// insert ecoutes
		for (EEcoute eecoute : EEcoute.values()) {
			Optional<Ecoute> findEcoute = ecouteRepository.findByName(eecoute);
			if (!findEcoute.isPresent()) {
				Ecoute ecoute = Ecoute.builder().name(eecoute).build();
				ecouteRepository.save(ecoute);
			}
		}
		
		

		// insert User Admin
		if (userRepository.findByUserName("admin").isEmpty()) {
			User user = User.builder().firstName("admin").lastName("super user").language("en").userName("admin")
					.password(encoder.encode("123456")).role(roleRepository.findByName(ERole.ROLE_ADMIN).get())
					.teams(new HashSet<>(Arrays.asList(tm))).build();
			userRepository.save(user);
		}

		System.out.println("Bienvenue dans le portail de vente");
	}
}