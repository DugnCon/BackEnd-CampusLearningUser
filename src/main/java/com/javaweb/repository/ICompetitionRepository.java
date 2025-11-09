package com.javaweb.repository;

import com.javaweb.entity.Competition.CompetitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICompetitionRepository extends JpaRepository<CompetitionEntity, Long> {
}
