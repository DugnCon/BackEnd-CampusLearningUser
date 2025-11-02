package com.javaweb.repository;

import com.javaweb.entity.Competition.CompetitionParticipantEntity;
import com.javaweb.entity.Competition.CompetitionProblemEntity;
import com.javaweb.entity.Competition.CompetitionSubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ICompetitionSubmissionRepository extends JpaRepository<CompetitionSubmissionEntity, Long> {
    @Query("SELECT cs FROM CompetitionSubmissionEntity cs WHERE cs.participant.participantID = :participantId AND cs.problem.problemID = :problemId ORDER BY cs.submittedAt")
    CompetitionSubmissionEntity findLatestByParticipantIdAndProblemId(@Param("participantId") Long participantId, @Param("problemId") Long problemId);

    @Query("SELECT cs FROM CompetitionSubmissionEntity cs WHERE cs.participant.participantID = :participantId AND cs.problem.problemID = :problemId ORDER BY cs.submittedAt DESC")
    List<CompetitionSubmissionEntity> findByParticipantIDdAndProblemID(@Param("participantId") Long participantId, @Param("problemId") Long problemId);

    boolean existsByParticipantAndProblemAndStatus(CompetitionParticipantEntity participant,
                                                   CompetitionProblemEntity problem,
                                                   String status);
    List<CompetitionSubmissionEntity> findByParticipant_ParticipantID(Long participantId);
}
