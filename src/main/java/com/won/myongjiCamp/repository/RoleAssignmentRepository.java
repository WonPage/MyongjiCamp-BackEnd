package com.won.myongjiCamp.repository;

import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.role.Role;
import com.won.myongjiCamp.model.board.role.RoleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleAssignmentRepository extends JpaRepository<RoleAssignment, Long> {
    Optional<RoleAssignment> findByBoardAndRole(Board board, Role role);
}
