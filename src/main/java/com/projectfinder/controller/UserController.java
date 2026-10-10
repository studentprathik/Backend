package com.projectfinder.controller;

import com.projectfinder.dto.*;
import com.projectfinder.service.SkillService;
import com.projectfinder.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final SkillService skillService;

    public UserController(UserService userService, SkillService skillService) {
        this.userService = userService;
        this.skillService = skillService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfileDto>> getUserProfile(@PathVariable Long id) {
        UserProfileDto profile = userService.getUserProfile(id);
        return ResponseEntity.ok(ApiResponse.ok("User profile retrieved successfully", profile));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateUserProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserProfileDto updated = userService.updateUserProfile(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updated));
    }

    @GetMapping("/{id}/skills")
    public ResponseEntity<ApiResponse<List<String>>> getUserSkills(@PathVariable Long id) {
        List<String> skills = skillService.getUserSkillNames(id);
        return ResponseEntity.ok(ApiResponse.ok("User skills retrieved successfully", skills));
    }

    @PutMapping("/{id}/skills")
    public ResponseEntity<ApiResponse<List<String>>> updateUserSkills(
            @PathVariable Long id,
            @RequestBody UpdateSkillsRequest request) {
        List<String> updatedSkills = skillService.updateUserSkills(id, request.getSkills());
        return ResponseEntity.ok(ApiResponse.ok("User skills updated successfully", updatedSkills));
    }

    @GetMapping("/{id}/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboard(@PathVariable Long id) {
        DashboardStatsDto dashboard = userService.getDashboard(id);
        return ResponseEntity.ok(ApiResponse.ok("Dashboard data retrieved successfully", dashboard));
    }
}
