package com.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.entity.Guesthouse;

public interface GuesthouseRepository extends JpaRepository<Guesthouse, Long> {

	interface GuesthouseSummary {
        Long getId();
        String getName();
        Integer getRoomCount();
        Double getRating();
    }

    @Query("""
        select g.id as id, g.name as name, g.roomCount as roomCount, g.rating as rating
        from Guesthouse g
        where g.host.id = :hostId
    """)
    List<GuesthouseSummary> findMyGuesthouses(@Param("hostId") Long hostId);
	
}
