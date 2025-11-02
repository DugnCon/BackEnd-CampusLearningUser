package com.javaweb.repository;

import com.javaweb.entity.Competition.CompetitionEntity;
import com.javaweb.entity.Competition.CompetitionParticipantEntity;
import com.javaweb.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ICompetitionParticipantRepository extends JpaRepository<CompetitionParticipantEntity, Long> {
    @Query("SELECT p FROM CompetitionParticipantEntity p WHERE p.competition.competitionID = :competitionId AND p.user.UserID = :userId")
    CompetitionParticipantEntity findByCompetitionIDAndUserID(@Param("competitionId") Long competitionId, @Param("userId") Long userId);
    @Query("SELECT cp FROM CompetitionParticipantEntity cp WHERE cp.competition = :competition AND cp.user = :user")
    CompetitionParticipantEntity findByCompetitionAndUser(@Param("competition") CompetitionEntity competition, @Param("user") UserEntity user);
    List<CompetitionParticipantEntity> findByCompetition_CompetitionIDAndStatus(Long competitionId, String status);
}
