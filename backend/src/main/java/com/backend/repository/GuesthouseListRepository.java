package com.backend.repository;

import com.backend.entity.Guesthouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuesthouseListRepository extends JpaRepository<Guesthouse, Integer> {
    // 이름으로 검색하고 싶다면 아래처럼 확장 가능
    // List<Guesthouse> findByNameContainingIgnoreCase(String name);
}
