package com.projectfinder.repository;

import com.projectfinder.entity.RequestStatus;
import com.projectfinder.entity.TeamRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRequestRepository extends JpaRepository<TeamRequest, Long> {

    List<TeamRequest> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    List<TeamRequest> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    Optional<TeamRequest> findByProjectIdAndStudentId(Long projectId, Long studentId);

    boolean existsByProjectIdAndStudentIdAndStatus(Long projectId, Long studentId, RequestStatus status);

    @Query("SELECT r FROM TeamRequest r WHERE r.project.owner.id = :ownerId ORDER BY r.createdAt DESC")
    List<TeamRequest> findIncomingRequestsByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT COUNT(r) FROM TeamRequest r WHERE r.project.owner.id = :ownerId AND r.status = 'PENDING'")
    long countPendingIncomingRequestsByOwnerId(@Param("ownerId") Long ownerId);

    long countByStudentIdAndStatus(Long studentId, RequestStatus status);
}
