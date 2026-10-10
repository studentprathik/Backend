package com.projectfinder.service;

import com.projectfinder.dto.SkillMatchResult;
import com.projectfinder.dto.TeamMemberDto;
import com.projectfinder.dto.TeamRequestResponseDto;
import com.projectfinder.dto.UserSummaryDto;
import com.projectfinder.entity.*;
import com.projectfinder.exception.BadRequestException;
import com.projectfinder.exception.ResourceNotFoundException;
import com.projectfinder.repository.ProjectRepository;
import com.projectfinder.repository.TeamMemberRepository;
import com.projectfinder.repository.TeamRequestRepository;
import com.projectfinder.repository.UserRepository;
import com.projectfinder.util.SkillMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamRequestService {

    private final TeamRequestRepository teamRequestRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final SkillService skillService;

    public TeamRequestService(TeamRequestRepository teamRequestRepository,
                              ProjectRepository projectRepository,
                              UserRepository userRepository,
                              TeamMemberRepository teamMemberRepository,
                              SkillService skillService) {
        this.teamRequestRepository = teamRequestRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.skillService = skillService;
    }

    @Transactional
    public TeamRequestResponseDto sendJoinRequest(Long projectId, Long studentId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + studentId));

        // Rule 3: Project owner cannot request to join their own project
        if (project.getOwner().getId().equals(studentId)) {
            throw new BadRequestException("You are the owner of this project.");
        }

        // Rule 4: A student cannot join a project twice
        if (teamMemberRepository.existsByProjectIdAndStudentId(projectId, studentId)) {
            throw new BadRequestException("You are already a member of this project.");
        }

        // Rule 5: Project team full
        long currentCount = teamMemberRepository.countByProjectId(projectId);
        if (currentCount >= project.getTeamSize() || project.getStatus() == ProjectStatus.FULL) {
            throw new BadRequestException("This project team is already full.");
        }

        // Rule 2: Cannot send multiple pending requests
        if (teamRequestRepository.existsByProjectIdAndStudentIdAndStatus(projectId, studentId, RequestStatus.PENDING)) {
            throw new BadRequestException("You already have a pending join request for this project.");
        }

        TeamRequest request = new TeamRequest(project, student);
        TeamRequest saved = teamRequestRepository.save(request);

        return toDto(saved);
    }

    public List<TeamRequestResponseDto> getRequestsByProject(Long projectId, Long currentUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        if (!project.getOwner().getId().equals(currentUserId)) {
            throw new BadRequestException("Only the project owner can view join requests for this project.");
        }

        return teamRequestRepository.findByProjectIdOrderByCreatedAtDesc(projectId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<TeamRequestResponseDto> getRequestsByStudent(Long studentId) {
        return teamRequestRepository.findByStudentIdOrderByCreatedAtDesc(studentId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<TeamRequestResponseDto> getIncomingRequestsForOwner(Long ownerId) {
        return teamRequestRepository.findIncomingRequestsByOwnerId(ownerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TeamRequestResponseDto acceptRequest(Long requestId, Long ownerId) {
        TeamRequest request = teamRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        Project project = request.getProject();
        if (!project.getOwner().getId().equals(ownerId)) {
            throw new BadRequestException("Only the project owner can accept join requests.");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be accepted. Current status: " + request.getStatus());
        }

        long currentCount = teamMemberRepository.countByProjectId(project.getId());
        if (currentCount >= project.getTeamSize()) {
            throw new BadRequestException("Cannot accept request: Project team is already full (" + project.getTeamSize() + "/" + project.getTeamSize() + ").");
        }

        request.setStatus(RequestStatus.ACCEPTED);
        TeamRequest savedRequest = teamRequestRepository.save(request);

        // Add to team members if not already
        if (!teamMemberRepository.existsByProjectIdAndStudentId(project.getId(), request.getStudent().getId())) {
            TeamMember newMember = new TeamMember(project, request.getStudent(), "MEMBER");
            teamMemberRepository.save(newMember);
        }

        // Update project status if full
        long newCount = teamMemberRepository.countByProjectId(project.getId());
        if (newCount >= project.getTeamSize()) {
            project.setStatus(ProjectStatus.FULL);
            projectRepository.save(project);
        }

        return toDto(savedRequest);
    }

    @Transactional
    public TeamRequestResponseDto rejectRequest(Long requestId, Long ownerId) {
        TeamRequest request = teamRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        Project project = request.getProject();
        if (!project.getOwner().getId().equals(ownerId)) {
            throw new BadRequestException("Only the project owner can reject join requests.");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be rejected. Current status: " + request.getStatus());
        }

        request.setStatus(RequestStatus.REJECTED);
        TeamRequest savedRequest = teamRequestRepository.save(request);

        return toDto(savedRequest);
    }

    public List<TeamMemberDto> getProjectMembers(Long projectId) {
        return teamMemberRepository.findByProjectIdOrderByJoinedAtAsc(projectId).stream().map(m -> {
            User s = m.getStudent();
            List<String> studentSkills = skillService.getUserSkillNames(s.getId());
            return new TeamMemberDto(
                    m.getId(),
                    s.getId(),
                    s.getName(),
                    s.getEmail(),
                    s.getDepartment(),
                    s.getYear(),
                    m.getRole(),
                    studentSkills,
                    m.getJoinedAt()
            );
        }).collect(Collectors.toList());
    }

    public TeamRequestResponseDto toDto(TeamRequest request) {
        TeamRequestResponseDto dto = new TeamRequestResponseDto();
        dto.setId(request.getId());
        dto.setProjectId(request.getProject().getId());
        dto.setProjectTitle(request.getProject().getTitle());
        dto.setStatus(request.getStatus());
        dto.setCreatedAt(request.getCreatedAt());

        User s = request.getStudent();
        dto.setStudent(new UserSummaryDto(s.getId(), s.getName(), s.getEmail(), s.getDepartment(), s.getYear()));

        List<String> studentSkills = skillService.getUserSkillNames(s.getId());
        dto.setStudentSkills(studentSkills);

        List<String> requiredSkills = skillService.getProjectSkillNames(request.getProject().getId());
        SkillMatchResult match = SkillMatcher.calculate(requiredSkills, studentSkills);
        dto.setSkillMatchPercentage(match.getPercentage());
        dto.setMatchedSkills(match.getMatched());
        dto.setMissingSkills(match.getMissing());

        return dto;
    }
}
