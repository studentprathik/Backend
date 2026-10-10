package com.projectfinder.dto;

public class AuthResponse {
    private Long id;
    private String name;
    private String email;
    private String department;
    private String year;
    private String role;
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(Long id, String name, String email, String department, String year, String role, String message) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.year = year;
        this.role = role;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
