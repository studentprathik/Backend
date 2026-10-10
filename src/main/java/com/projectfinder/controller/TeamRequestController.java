package com.projectfinder.controller;

import com.projectfinder.dto.ApiResponse;
import com.projectfinder.dto.TeamMemberDto;
import com.projectfinder.dto.TeamRequestResponseDto;
import com.projectfinder.service.TeamRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class TeamRequestController {

    private final TeamRequestService teamRequestService;

    public TeamRequestController(TeamRequestService teamRequestService) {
        this.teamRequestService = teamRequestService;
    }

    // POST /api/projects/{projectId}/requests
    @PostMapping("/api/projects/{projectId}/requests")
    public ResponseEntity<ApiResponse<TeamRequestResponseDto>> sendJoinRequest(
            @PathVariable Long projectId,
            @RequestBody Map<String, Long> payload) {
        Long studentId = payload.get("studentId");
        if (studentId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Student ID is required"));
        }
        TeamRequestResponseDto response = teamRequestService.sendJoinRequest(projectId, studentId);
        return new ResponseEntity<>(ApiResponse.ok("Join request submitted successfully", response), HttpStatus.CREATED);
    }

    // GET /api/projects/{projectId}/requests
    @GetMapping("/api/projects/{projectId}/requests")
    public ResponseEntity<ApiResponse<List<TeamRequestResponseDto>>> getRequestsByProject(
            @PathVariable Long projectId,
            @RequestParam Long userId) {
        List<TeamRequestResponseDto> requests = teamRequestService.getRequestsByProject(projectId, userId);
        return ResponseEntity.ok(ApiResponse.ok("Project requests retrieved successfully", requests));
    }

    // GET /api/users/{userId}/requests (Student's outgoing requests)
    @GetMapping("/api/users/{userId}/requests")
    public ResponseEntity<ApiResponse<List<TeamRequestResponseDto>>> getStudentRequests(@PathVariable Long userId) {
        List<TeamRequestResponseDto> requests = teamRequestService.getRequestsByStudent(userId);
        return ResponseEntity.ok(ApiResponse.ok("Student requests retrieved successfully", requests));
    }

    // GET /api/users/{userId}/incoming-requests (Requests for owner's projects)
    @GetMapping("/api/users/{userId}/incoming-requests")
    public ResponseEntity<ApiResponse<List<TeamRequestResponseDto>>> getIncomingRequests(@PathVariable Long userId) {
        List<TeamRequestResponseDto> requests = teamRequestService.getIncomingRequestsForOwner(userId);
        return ResponseEntity.ok(ApiResponse.ok("Incoming requests retrieved successfully", requests));
    }

    // PUT /api/requests/{requestId}/accept
    @PutMapping("/api/requests/{requestId}/accept")
    public ResponseEntity<ApiResponse<TeamRequestResponseDto>> acceptRequest(
            @PathVariable Long requestId,
            @RequestParam Long ownerId) {
        TeamRequestResponseDto response = teamRequestService.acceptRequest(requestId, ownerId);
        return ResponseEntity.ok(ApiResponse.ok("Join request accepted successfully", response));
    }

    // PUT /api/requests/{requestId}/reject
    @PutMapping("/api/requests/{requestId}/reject")
    public ResponseEntity<ApiResponse<TeamRequestResponseDto>> rejectRequest(
            @PathVariable Long requestId,
            @RequestParam Long ownerId) {
        TeamRequestResponseDto response = teamRequestService.rejectRequest(requestId, ownerId);
        return ResponseEntity.ok(ApiResponse.ok("Join request rejected", response));
    }

    // GET /api/projects/{projectId}/members
    @GetMapping("/api/projects/{projectId}/members")
    public ResponseEntity<ApiResponse<List<TeamMemberDto>>> getProjectMembers(@PathVariable Long projectId) {
        List<TeamMemberDto> members = teamRequestService.getProjectMembers(projectId);
        return ResponseEntity.ok(ApiResponse.ok("Project members retrieved successfully", members));
    }
}
