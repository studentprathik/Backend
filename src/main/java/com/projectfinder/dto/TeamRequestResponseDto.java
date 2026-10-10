package com.projectfinder.dto;

import com.projectfinder.entity.RequestStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TeamRequestResponseDto {
    private Long id;
    private Long projectId;
    private String projectTitle;
    private UserSummaryDto student;
    private List<String> studentSkills = new ArrayList<>();
    private int skillMatchPercentage;
    private List<String> matchedSkills = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();
    private RequestStatus status;
    private LocalDateTime createdAt;

    public TeamRequestResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public UserSummaryDto getStudent() {
        return student;
    }

    public void setStudent(UserSummaryDto student) {
        this.student = student;
    }

    public List<String> getStudentSkills() {
        return studentSkills;
    }

    public void setStudentSkills(List<String> studentSkills) {
        this.studentSkills = studentSkills;
    }

    public int getSkillMatchPercentage() {
        return skillMatchPercentage;
    }

    public void setSkillMatchPercentage(int skillMatchPercentage) {
        this.skillMatchPercentage = skillMatchPercentage;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
