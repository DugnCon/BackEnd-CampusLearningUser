package com.javaweb.repository;

import com.javaweb.entity.Competition.CompetitionRegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ICompetitionRegistrationRepository extends JpaRepository<CompetitionRegistrationEntity, Long> {
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM CompetitionRegistrationEntity r WHERE r.competition.competitionID = :competitionId AND r.user.UserID = :userId")
    boolean existsByCompetitionIDAndUserID(@Param("competitionId") Long competitionId, @Param("userId") Long userId);
    //@Query("SELECT r FROM CompetitionRegistrationEntity r WHERE r.competition.competitionID = :competitionId AND r.user.UserID = :userId")
    //CompetitionRegistrationEntity findByCompetitionIDAndUserIDForStart(@Param("competitionId") Long competitionId, @Param("userId") Long userId);
}
