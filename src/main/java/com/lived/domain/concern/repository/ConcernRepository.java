package com.lived.domain.concern.repository;

import com.lived.domain.concern.entity.Concern;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConcernRepository extends JpaRepository<Concern, Long> {
    Optional<Concern> findByName(String name);
}
