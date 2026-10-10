package com.projectfinder.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TeamMemberDto {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String department;
    private String year;
    private String role;
    private List<String> skills = new ArrayList<>();
    private LocalDateTime joinedAt;

    public TeamMemberDto() {
    }

    public TeamMemberDto(Long id, Long studentId, String studentName, String studentEmail, String department, String year, String role, List<String> skills, LocalDateTime joinedAt) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.department = department;
        this.year = year;
        this.role = role;
        this.skills = skills;
        this.joinedAt = joinedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
