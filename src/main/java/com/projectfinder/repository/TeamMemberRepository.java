package com.projectfinder.repository;

import com.projectfinder.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findByProjectIdOrderByJoinedAtAsc(Long projectId);
    List<TeamMember> findByStudentIdOrderByJoinedAtDesc(Long studentId);
    long countByProjectId(Long projectId);
    boolean existsByProjectIdAndStudentId(Long projectId, Long studentId);
    Optional<TeamMember> findByProjectIdAndStudentId(Long projectId, Long studentId);
    long countByStudentId(Long studentId);
}
