package com.won.myongjiCamp.repository;

import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.role.Role;
import com.won.myongjiCamp.model.board.role.RoleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleAssignmentRepository extends JpaRepository<RoleAssignment, Long> {
    Optional<RoleAssignment> findByBoardAndRole(Board board, Role role);

    void deleteByBoardAndRole(Board board, Role role);

    List<RoleAssignment> findByBoard(Board board);
}
