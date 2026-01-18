package com.lived.domain.routine.repository;

import com.lived.domain.routine.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
}
