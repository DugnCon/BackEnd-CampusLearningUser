package com.javaweb.repository;

import com.javaweb.entity.Competition.CompetitionProblemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ICompetitionProblemRepository extends JpaRepository<CompetitionProblemEntity, Long> {
    @Query("SELECT cp FROM CompetitionProblemEntity cp WHERE cp.competition.competitionID = :competitionId ORDER BY cp.problemID")
    List<CompetitionProblemEntity> findByCompetition_CompetitionID(@Param("competitionId") Long competitionId);
    CompetitionProblemEntity findByCompetition_CompetitionIDAndProblemID(Long competitionId, Long problemId);

}
