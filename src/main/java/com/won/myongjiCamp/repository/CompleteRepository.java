package com.won.myongjiCamp.repository;

import com.won.myongjiCamp.model.board.CompleteBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompleteRepository extends JpaRepository<CompleteBoard,Long> {
}
