package com.won.myongjiCamp.repository;

import com.won.myongjiCamp.model.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BoardRepository extends JpaRepository<Board,Long>, JpaSpecificationExecutor<Board> {
}
