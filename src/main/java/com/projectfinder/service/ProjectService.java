package com.projectfinder.service;

import com.projectfinder.dto.*;
import com.projectfinder.entity.*;
import com.projectfinder.exception.BadRequestException;
import com.projectfinder.exception.ResourceNotFoundException;
import com.projectfinder.repository.*;
import com.projectfinder.util.SkillMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRequestRepository teamRequestRepository;
    private final SkillService skillService;

    public ProjectService(ProjectRepository projectRepository,
                          UserRepository userRepository,
                          TeamMemberRepository teamMemberRepository,
                          TeamRequestRepository teamRequestRepository,
                          SkillService skillService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamRequestRepository = teamRequestRepository;
        this.skillService = skillService;
    }

    @Transactional
    public ProjectResponseDto createProject(CreateProjectRequest request) {
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner user not found with id: " + request.getOwnerId()));

        Project project = new Project();
        project.setTitle(request.getTitle().trim());
        project.setDescription(request.getDescription().trim());
        project.setCategory(request.getCategory().trim());
        project.setTeamSize(request.getTeamSize());
        project.setOwner(owner);
        project.setStatus(ProjectStatus.OPEN);

        Project savedProject = projectRepository.save(project);

        // Save skills
        skillService.setProjectSkills(savedProject, request.getRequiredSkills());

        // Automatically add owner as first team member
        TeamMember ownerMember = new TeamMember(savedProject, owner, "OWNER");
        teamMemberRepository.save(ownerMember);

        return toDto(savedProject, owner.getId());
    }

    public List<ProjectResponseDto> getAllProjects(Long currentUserId) {
        List<Project> projects = projectRepository.findAll();
        // Sort newest first
        projects.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return projects.stream()
                .map(p -> toDto(p, currentUserId))
                .collect(Collectors.toList());
    }

    public ProjectResponseDto getProjectById(Long projectId, Long currentUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        return toDto(project, currentUserId);
    }

    @Transactional
    public ProjectResponseDto updateProject(Long projectId, Long userId, UpdateProjectRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        if (!project.getOwner().getId().equals(userId)) {
            throw new BadRequestException("Only the project owner can edit this project.");
        }

        long currentCount = teamMemberRepository.countByProjectId(projectId);
        if (request.getTeamSize() < currentCount) {
            throw new BadRequestException("Team size cannot be less than current member count (" + currentCount + ").");
        }

        project.setTitle(request.getTitle().trim());
        project.setDescription(request.getDescription().trim());
        project.setCategory(request.getCategory().trim());
        project.setTeamSize(request.getTeamSize());

        if (currentCount >= request.getTeamSize()) {
            project.setStatus(ProjectStatus.FULL);
        } else {
            project.setStatus(ProjectStatus.OPEN);
        }

        Project updated = projectRepository.save(project);
        skillService.setProjectSkills(updated, request.getRequiredSkills());

        return toDto(updated, userId);
    }

    @Transactional
    public void deleteProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        if (!project.getOwner().getId().equals(userId)) {
            throw new BadRequestException("Only the project owner can delete this project.");
        }

        // Clean related entities
        List<TeamRequest> requests = teamRequestRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
        teamRequestRepository.deleteAll(requests);

        List<TeamMember> members = teamMemberRepository.findByProjectIdOrderByJoinedAtAsc(projectId);
        teamMemberRepository.deleteAll(members);

        skillService.setProjectSkills(project, Collections.emptyList());

        projectRepository.delete(project);
    }

    public List<ProjectResponseDto> searchProjects(String keyword, String category, String department, String skill, Long currentUserId) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanCategory = (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("all")) ? category.trim() : null;
        String cleanDept = (department != null && !department.trim().isEmpty() && !department.equalsIgnoreCase("all")) ? department.trim() : null;
        String cleanSkill = (skill != null && !skill.trim().isEmpty() && !skill.equalsIgnoreCase("all")) ? skill.trim() : null;

        List<Project> projects = projectRepository.searchProjects(cleanKeyword, cleanCategory, cleanDept, cleanSkill);
        return projects.stream()
                .map(p -> toDto(p, currentUserId))
                .collect(Collectors.toList());
    }

    public List<ProjectResponseDto> getMyCreatedProjects(Long userId) {
        List<Project> projects = projectRepository.findByOwnerIdOrderByCreatedAtDesc(userId);
        return projects.stream()
                .map(p -> toDto(p, userId))
                .collect(Collectors.toList());
    }

    public List<ProjectResponseDto> getMyJoinedProjects(Long userId) {
        List<TeamMember> memberships = teamMemberRepository.findByStudentIdOrderByJoinedAtDesc(userId);
        // Exclude projects where user is owner so "Joined" section clearly represents joined teams
        return memberships.stream()
                .filter(m -> !m.getProject().getOwner().getId().equals(userId))
                .map(m -> toDto(m.getProject(), userId))
                .collect(Collectors.toList());
    }

    public ProjectResponseDto toDto(Project project, Long currentUserId) {
        ProjectResponseDto dto = new ProjectResponseDto();
        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setDescription(project.getDescription());
        dto.setCategory(project.getCategory());
        dto.setTeamSize(project.getTeamSize());
        dto.setStatus(project.getStatus());
        dto.setCreatedAt(project.getCreatedAt());

        User owner = project.getOwner();
        dto.setOwner(new UserSummaryDto(owner.getId(), owner.getName(), owner.getEmail(), owner.getDepartment(), owner.getYear()));

        List<String> requiredSkills = skillService.getProjectSkillNames(project.getId());
        dto.setRequiredSkills(requiredSkills);

        long memberCount = teamMemberRepository.countByProjectId(project.getId());
        dto.setCurrentMemberCount(memberCount);

        List<TeamMember> members = teamMemberRepository.findByProjectIdOrderByJoinedAtAsc(project.getId());
        List<TeamMemberDto> memberDtos = members.stream().map(m -> {
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
        dto.setMembers(memberDtos);

        if (currentUserId != null) {
            boolean isOwner = owner.getId().equals(currentUserId);
            boolean isMember = teamMemberRepository.existsByProjectIdAndStudentId(project.getId(), currentUserId);
            boolean hasPending = teamRequestRepository.existsByProjectIdAndStudentIdAndStatus(project.getId(), currentUserId, RequestStatus.PENDING);

            dto.setOwner(isOwner);
            dto.setMember(isMember);
            dto.setHasPendingRequest(hasPending);

            List<String> studentSkills = skillService.getUserSkillNames(currentUserId);
            SkillMatchResult matchResult = SkillMatcher.calculate(requiredSkills, studentSkills);
            dto.setSkillMatchPercentage(matchResult.getPercentage());
            dto.setMatchedSkills(matchResult.getMatched());
            dto.setMissingSkills(matchResult.getMissing());
        } else {
            dto.setOwner(false);
            dto.setMember(false);
            dto.setHasPendingRequest(false);
            dto.setSkillMatchPercentage(0);
            dto.setMatchedSkills(Collections.emptyList());
            dto.setMissingSkills(requiredSkills);
        }

        return dto;
    }
}
