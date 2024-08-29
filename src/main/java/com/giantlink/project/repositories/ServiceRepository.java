package com.giantlink.project.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.giantlink.project.entities.Service;
import com.giantlink.project.models.responses.TopService;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long>{
	
	Optional<Service> findByServiceName(String serviceName);
	
	Page<Service> findByServiceNameContainingIgnoreCase(String name, Pageable pageable);
	
	@Query(value = "SELECT  ls.service_id as id, s.service_name, Round(sum(s.point),2) as point ,COUNT(ls.service_id) as leadsNumber "
			+ " , s.statut ,s.service_type_id, s.timestamp FROM `leads_services` ls, `services` s WHERE s.id = ls.service_id GROUP BY service_id", nativeQuery = true)
	List<Service> getTopServices();
	
	@Query(value= "select ls.service_id, COUNT(ls.service_id) as leadsNumber FROM `leads_services` ls where ls.service_id = :serviceId " , nativeQuery = true)
	TopService getLeadsNumber(@Param("serviceId")Long serviceId);
	
	@Query(value= "SELECT COUNT(ls.service_id) FROM `leads_services` ls , `leads` l where DATE(timestamp) = CURRENT_DATE "
			+ "AND ls.lead_id = l.id" , nativeQuery = true)
	Long getNumberOfSoldServicesPerDay();
	
	@Query(value= "SELECT Round(SUM(s.point),2) FROM `leads_services` ls , `leads` l, `services` s where DATE(l.timestamp) = CURRENT_DATE "
			+ "and ls.lead_id = l.id and ls.service_id = s.id" , nativeQuery = true)
	Float getTotalPointsOfSoldServicesPerDay();
	
	
	
	

}
