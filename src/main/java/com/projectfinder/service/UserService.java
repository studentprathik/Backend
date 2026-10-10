package com.projectfinder.service;

import com.projectfinder.dto.*;
import com.projectfinder.entity.RequestStatus;
import com.projectfinder.entity.User;
import com.projectfinder.exception.ResourceNotFoundException;
import com.projectfinder.repository.ProjectRepository;
import com.projectfinder.repository.TeamMemberRepository;
import com.projectfinder.repository.TeamRequestRepository;
import com.projectfinder.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SkillService skillService;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRequestRepository teamRequestRepository;
    private final ProjectService projectService;

    public UserService(UserRepository userRepository,
                       SkillService skillService,
                       ProjectRepository projectRepository,
                       TeamMemberRepository teamMemberRepository,
                       TeamRequestRepository teamRequestRepository,
                       ProjectService projectService) {
        this.userRepository = userRepository;
        this.skillService = skillService;
        this.projectRepository = projectRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamRequestRepository = teamRequestRepository;
        this.projectService = projectService;
    }

    public User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public UserProfileDto getUserProfile(Long id) {
        User user = getUserEntity(id);
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setDepartment(user.getDepartment());
        dto.setYear(user.getYear());
        dto.setBio(user.getBio());
        dto.setInterests(user.getInterests());
        dto.setPreviousProjects(user.getPreviousProjects());
        dto.setRole(user.getRole());
        dto.setSkills(skillService.getUserSkillNames(user.getId()));
        return dto;
    }

    @Transactional
    public UserProfileDto updateUserProfile(Long id, UpdateProfileRequest request) {
        User user = getUserEntity(id);
        user.setName(request.getName().trim());
        user.setDepartment(request.getDepartment().trim());
        user.setYear(request.getYear().trim());
        user.setBio(request.getBio());
        user.setInterests(request.getInterests());
        user.setPreviousProjects(request.getPreviousProjects());

        User updated = userRepository.save(user);

        UserProfileDto dto = new UserProfileDto();
        dto.setId(updated.getId());
        dto.setName(updated.getName());
        dto.setEmail(updated.getEmail());
        dto.setDepartment(updated.getDepartment());
        dto.setYear(updated.getYear());
        dto.setBio(updated.getBio());
        dto.setInterests(updated.getInterests());
        dto.setPreviousProjects(updated.getPreviousProjects());
        dto.setRole(updated.getRole());
        dto.setSkills(skillService.getUserSkillNames(updated.getId()));
        return dto;
    }

    public DashboardStatsDto getDashboard(Long userId) {
        User user = getUserEntity(userId);
        DashboardStatsDto dto = new DashboardStatsDto();
        dto.setStudentName(user.getName());
        dto.setDepartment(user.getDepartment());
        dto.setYear(user.getYear());

        long createdCount = projectRepository.findByOwnerIdOrderByCreatedAtDesc(userId).size();
        dto.setProjectsCreated(createdCount);

        long joinedCount = teamMemberRepository.findByStudentIdOrderByJoinedAtDesc(userId).stream()
                .filter(m -> !m.getProject().getOwner().getId().equals(userId))
                .count();
        dto.setProjectsJoined(joinedCount);

        long pendingIncoming = teamRequestRepository.countPendingIncomingRequestsByOwnerId(userId);
        long pendingOutgoing = teamRequestRepository.countByStudentIdAndStatus(userId, RequestStatus.PENDING);
        // Display total pending requests relevant to student (incoming + outgoing)
        dto.setPendingRequests(pendingIncoming + pendingOutgoing);

        List<String> userSkills = skillService.getUserSkillNames(userId);
        dto.setSkills(userSkills);

        List<ProjectResponseDto> myProjects = projectService.getMyCreatedProjects(userId);
        dto.setMyProjects(myProjects);

        // Recommended / available projects: all projects not owned by this user, ordered by match % descending
        List<ProjectResponseDto> allProjects = projectService.getAllProjects(userId);
        List<ProjectResponseDto> recommended = allProjects.stream()
                .filter(p -> !p.isOwner() && !p.isMember())
                .sorted(Comparator.comparingInt(ProjectResponseDto::getSkillMatchPercentage).reversed())
                .limit(6)
                .collect(Collectors.toList());
        dto.setRecommendedProjects(recommended);

        return dto;
    }
}
