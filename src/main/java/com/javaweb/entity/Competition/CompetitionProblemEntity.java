package com.javaweb.entity.Competition;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.javaweb.converter.TestCaseConverter;
import com.javaweb.model.dto.TestCasesDTO;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "competitionproblems")
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class CompetitionProblemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProblemID")
    private Long problemID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competitionID")
    private CompetitionEntity competition;

    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private List<CompetitionSubmissionEntity> submission;

    @Column(name = "Title", length = 200)
    private String title;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "Difficulty", length = 20)
    private String difficulty;

    @Column(name = "Points")
    private Integer points;

    @Column(name = "TimeLimit")
    private Integer timeLimit;

    @Column(name = "MemoryLimit")
    private Integer memoryLimit;

    @Column(name = "InputFormat", columnDefinition = "TEXT")
    private String inputFormat;

    @Column(name = "OutputFormat", columnDefinition = "TEXT")
    private String outputFormat;

    @Column(name = "Constraints", columnDefinition = "TEXT")
    private String constraints;

    @Column(name = "SampleInput", columnDefinition = "TEXT")
    private String sampleInput;

    @Column(name = "SampleOutput", columnDefinition = "TEXT")
    private String sampleOutput;

    @Column(name = "Explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "ImageURL", length = 500)
    private String imageURL;

    @Column(name = "StarterCode", columnDefinition = "TEXT")
    private String starterCode;

    @Convert(converter = TestCaseConverter.class)
    @Column(name = "TestCasesVisible")
    private List<TestCasesDTO> testCasesVisible;

    @Convert(converter = TestCaseConverter.class)
    @Column(name = "TestCasesHidden")
    private List<TestCasesDTO> testCasesHidden;

    @Column(name = "Tags", length = 500)
    private String tags;

    @Column(name = "Instructions", columnDefinition = "TEXT")
    private String instructions;

    public CompetitionProblemEntity() {
    }

    // Getters and Setters

    @JsonBackReference
    public List<CompetitionSubmissionEntity> getSubmission() {
        return submission;
    }

    public List<TestCasesDTO> getTestCasesHidden() {
        return testCasesHidden;
    }

    public void setTestCasesHidden(List<TestCasesDTO> testCasesHidden) {
        this.testCasesHidden = testCasesHidden;
    }

    public void setSubmission(List<CompetitionSubmissionEntity> submission) {
        this.submission = submission;
    }

    public Long getProblemID() {
        return problemID;
    }

    public void setProblemID(Long problemID) {
        this.problemID = problemID;
    }

    public CompetitionEntity getCompetition() {
        return competition;
    }

    public void setCompetition(CompetitionEntity competition) {
        this.competition = competition;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(Integer memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public String getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    public String getSampleInput() {
        return sampleInput;
    }

    public void setSampleInput(String sampleInput) {
        this.sampleInput = sampleInput;
    }

    public String getSampleOutput() {
        return sampleOutput;
    }

    public void setSampleOutput(String sampleOutput) {
        this.sampleOutput = sampleOutput;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStarterCode() {
        return starterCode;
    }

    public void setStarterCode(String starterCode) {
        this.starterCode = starterCode;
    }

    public List<TestCasesDTO> getTestCasesVisible() {
        return testCasesVisible;
    }

    public void setTestCasesVisible(List<TestCasesDTO> testCasesVisible) {
        this.testCasesVisible = testCasesVisible;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}