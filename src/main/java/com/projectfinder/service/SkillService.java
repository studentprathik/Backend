package com.projectfinder.service;

import com.projectfinder.entity.Skill;
import com.projectfinder.entity.User;
import com.projectfinder.entity.UserSkill;
import com.projectfinder.entity.Project;
import com.projectfinder.entity.ProjectSkill;
import com.projectfinder.exception.ResourceNotFoundException;
import com.projectfinder.repository.SkillRepository;
import com.projectfinder.repository.UserRepository;
import com.projectfinder.repository.UserSkillRepository;
import com.projectfinder.repository.ProjectSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final ProjectSkillRepository projectSkillRepository;

    public SkillService(SkillRepository skillRepository,
                        UserRepository userRepository,
                        UserSkillRepository userSkillRepository,
                        ProjectSkillRepository projectSkillRepository) {
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.userSkillRepository = userSkillRepository;
        this.projectSkillRepository = projectSkillRepository;
    }

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public Skill getOrCreateSkill(String name) {
        String cleanName = name.trim();
        return skillRepository.findByNameIgnoreCase(cleanName)
                .orElseGet(() -> skillRepository.save(new Skill(cleanName)));
    }

    public List<String> getUserSkillNames(Long userId) {
        return userSkillRepository.findByUserId(userId).stream()
                .map(us -> us.getSkill().getName())
                .collect(Collectors.toList());
    }

    @Transactional
    public List<String> updateUserSkills(Long userId, List<String> skillNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        userSkillRepository.deleteByUserId(userId);

        if (skillNames == null || skillNames.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> updated = new ArrayList<>();
        for (String skillName : skillNames) {
            if (skillName != null && !skillName.trim().isEmpty()) {
                Skill skill = getOrCreateSkill(skillName.trim());
                userSkillRepository.save(new UserSkill(user, skill));
                updated.add(skill.getName());
            }
        }
        return updated;
    }

    public List<String> getProjectSkillNames(Long projectId) {
        return projectSkillRepository.findByProjectId(projectId).stream()
                .map(ps -> ps.getSkill().getName())
                .collect(Collectors.toList());
    }

    @Transactional
    public void setProjectSkills(Project project, List<String> skillNames) {
        projectSkillRepository.deleteByProjectId(project.getId());

        if (skillNames != null) {
            for (String skillName : skillNames) {
                if (skillName != null && !skillName.trim().isEmpty()) {
                    Skill skill = getOrCreateSkill(skillName.trim());
                    projectSkillRepository.save(new ProjectSkill(project, skill));
                }
            }
        }
    }
}
