package com.projectfinder.dto;

import java.util.ArrayList;
import java.util.List;

public class DashboardStatsDto {
    private String studentName;
    private String department;
    private String year;
    private long projectsCreated;
    private long projectsJoined;
    private long pendingRequests;
    private List<String> skills = new ArrayList<>();
    private List<ProjectResponseDto> myProjects = new ArrayList<>();
    private List<ProjectResponseDto> recommendedProjects = new ArrayList<>();

    public DashboardStatsDto() {
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public long getProjectsCreated() {
        return projectsCreated;
    }

    public void setProjectsCreated(long projectsCreated) {
        this.projectsCreated = projectsCreated;
    }

    public long getProjectsJoined() {
        return projectsJoined;
    }

    public void setProjectsJoined(long projectsJoined) {
        this.projectsJoined = projectsJoined;
    }

    public long getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(long pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<ProjectResponseDto> getMyProjects() {
        return myProjects;
    }

    public void setMyProjects(List<ProjectResponseDto> myProjects) {
        this.myProjects = myProjects;
    }

    public List<ProjectResponseDto> getRecommendedProjects() {
        return recommendedProjects;
    }

    public void setRecommendedProjects(List<ProjectResponseDto> recommendedProjects) {
        this.recommendedProjects = recommendedProjects;
    }
}
