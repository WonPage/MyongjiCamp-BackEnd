package com.won.myongjiCamp.repository;

import com.won.myongjiCamp.model.Resume;
import com.won.myongjiCamp.model.board.role.RoleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleAssignment,Long> {
}
