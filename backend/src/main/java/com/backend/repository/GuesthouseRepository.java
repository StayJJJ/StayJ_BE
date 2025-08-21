package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entity.Guesthouse;

public interface GuesthouseRepository extends JpaRepository<Guesthouse, Integer> {
}
