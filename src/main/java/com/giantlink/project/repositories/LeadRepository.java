package com.giantlink.project.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.giantlink.project.entities.Ecoute;
import com.giantlink.project.entities.Lead;
import com.giantlink.project.entities.Product;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long>{
	
//	@Query(value = "SELECT * FROM `leads` ld JOIN `products` pr JOIN `ecoutes` ec WHERE (ld.encode = :encode or :encode is null ) AND (ec.id LIKE :ecouteId or :ecouteId is null)  AND (pr.product_name LIKE %:productName% or :productName is null )", 
//			nativeQuery = true, countQuery = "SELECT count(*) FROM `leads`")
//		Page<Lead> findByEncodeAndEcouteAndProduct(@Param("encode") Boolean encode, @Param("ecouteId") Long ecoute,@Param("productName") String productName, Pageable pageable);
//	
	
	@Query("select l from Lead l where (l.encode = :encode or :encode is null)" +
            "   and (l.ecoute = :ecoute or :ecoute is null)" +
            "  and (l.product = :product or :product is null)" +
            "  and (l.coachValidation = :coachValidation or :coachValidation is null)" +
            " and (l.employeeId = :employeeId or :employeeId is null)")
    Page<Lead> findByEncodeAndEcouteAndProduct(@Param("encode")Boolean encode , @Param("ecoute") Ecoute ecoute , @Param("product")Product product , 
    		@Param("employeeId")Long employeeId , @Param("coachValidation")Boolean coachValidation , Pageable pageable );
	
	//@Query("select new Lead(l.id, l.employeeId, max(l.totalPoint) )  from Lead l group by l.employeeId order by l.totalPoint DESC")
	@Query("select new Lead(l.id, l.employeeId, Round(sum(totalPoint),2) as tot ) from Lead l where DATE(l.timestamp) = CURRENT_DATE group by l.employeeId order by tot DESC")
	List<Lead> getSortedDataByTotalPoint();
	
	@Query("SELECT COUNT(l.id) FROM Lead l where DATE(l.timestamp) = CURRENT_DATE")
	Long getNumberOfLeadsPerDay();
	
	
	
	
		// Page<Lead> findByEncodeAndEcouteAndProduct(Boolean encode, Ecoute ecoute, Product product, Pageable pageable);
}
