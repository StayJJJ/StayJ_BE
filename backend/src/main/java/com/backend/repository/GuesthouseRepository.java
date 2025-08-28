package com.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.entity.Guesthouse;

public interface GuesthouseRepository extends JpaRepository<Guesthouse, Integer> {
	interface GuesthouseSummary {
		Integer getId();
		String getName();
		Integer getRoomCount();
		Double getRating();
		Integer getPhotoId();
	}

	@Query("""
		    select g.id as id, g.name as name, g.roomCount as roomCount, g.rating as rating, g.photoId as photoId
		    from Guesthouse g
		    where g.host.id = :hostId
			""")
	List<GuesthouseSummary> findMyGuesthouses(@Param("hostId") Integer hostId);

	@Query("""
			select coalesce(max(g.photoId), 0) from Guesthouse g
			""")
	Integer findMaxPhotoId();
	
    @Query("SELECT AVG(r.rating) " +
            "FROM Review r " +
            "WHERE r.reservation.room.guesthouse.id = :guesthouseId")
    Double calculateAverageRating(@Param("guesthouseId") Integer guesthouseId);
}
