package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.entity.Guesthouse;

@Repository
public interface GuesthouseRepository extends JpaRepository<Guesthouse, Long> {
}
