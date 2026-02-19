package com.lived.domain.routine.repository;

import com.lived.domain.routine.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    @Query("select r from Routine r join fetch r.category")
    List<Routine> findAllWithCategory();
}
