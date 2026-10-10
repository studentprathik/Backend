package com.projectfinder.repository;

import com.projectfinder.entity.ProjectSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectSkillRepository extends JpaRepository<ProjectSkill, Long> {
    List<ProjectSkill> findByProjectId(Long projectId);

    @Modifying
    @Query("DELETE FROM ProjectSkill ps WHERE ps.project.id = :projectId")
    void deleteByProjectId(@Param("projectId") Long projectId);

    boolean existsByProjectIdAndSkillId(Long projectId, Long skillId);
}
