package com.projectfinder.repository;

import com.projectfinder.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    @Query("SELECT DISTINCT p FROM Project p " +
           "LEFT JOIN ProjectSkill ps ON ps.project.id = p.id " +
           "LEFT JOIN Skill s ON s.id = ps.skill.id " +
           "WHERE (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:category IS NULL OR LOWER(p.category) = LOWER(:category)) " +
           "AND (:department IS NULL OR LOWER(p.owner.department) = LOWER(:department)) " +
           "AND (:skill IS NULL OR LOWER(s.name) = LOWER(:skill)) " +
           "ORDER BY p.createdAt DESC")
    List<Project> searchProjects(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("department") String department,
            @Param("skill") String skill
    );
}
