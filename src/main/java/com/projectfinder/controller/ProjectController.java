package com.projectfinder.controller;

import com.projectfinder.dto.ApiResponse;
import com.projectfinder.dto.CreateProjectRequest;
import com.projectfinder.dto.ProjectResponseDto;
import com.projectfinder.dto.UpdateProjectRequest;
import com.projectfinder.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponseDto>> createProject(
            @Valid @RequestBody CreateProjectRequest request) {
        ProjectResponseDto project = projectService.createProject(request);
        return new ResponseEntity<>(ApiResponse.ok("Project created successfully", project), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponseDto>>> getAllProjects(
            @RequestParam(required = false) Long userId) {
        List<ProjectResponseDto> projects = projectService.getAllProjects(userId);
        return ResponseEntity.ok(ApiResponse.ok("Projects retrieved successfully", projects));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponseDto>> getProjectById(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        ProjectResponseDto project = projectService.getProjectById(id, userId);
        return ResponseEntity.ok(ApiResponse.ok("Project details retrieved successfully", project));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponseDto>> updateProject(
            @PathVariable Long id,
            @RequestParam Long userId,
            @Valid @RequestBody UpdateProjectRequest request) {
        ProjectResponseDto updated = projectService.updateProject(id, userId, request);
        return ResponseEntity.ok(ApiResponse.ok("Project updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable Long id,
            @RequestParam Long userId) {
        projectService.deleteProject(id, userId);
        return ResponseEntity.ok(ApiResponse.ok("Project deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProjectResponseDto>>> searchProjects(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) Long userId) {
        List<ProjectResponseDto> projects = projectService.searchProjects(keyword, category, department, skill, userId);
        return ResponseEntity.ok(ApiResponse.ok("Filtered projects retrieved successfully", projects));
    }

    @GetMapping("/my-created/{userId}")
    public ResponseEntity<ApiResponse<List<ProjectResponseDto>>> getMyCreatedProjects(@PathVariable Long userId) {
        List<ProjectResponseDto> projects = projectService.getMyCreatedProjects(userId);
        return ResponseEntity.ok(ApiResponse.ok("Created projects retrieved successfully", projects));
    }

    @GetMapping("/my-joined/{userId}")
    public ResponseEntity<ApiResponse<List<ProjectResponseDto>>> getMyJoinedProjects(@PathVariable Long userId) {
        List<ProjectResponseDto> projects = projectService.getMyJoinedProjects(userId);
        return ResponseEntity.ok(ApiResponse.ok("Joined projects retrieved successfully", projects));
    }
}
