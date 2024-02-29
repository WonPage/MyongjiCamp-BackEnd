package com.won.myongjiCamp.repository;

import com.won.myongjiCamp.model.Comment;
import com.won.myongjiCamp.model.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
