package com.won.myongjiCamp.repository;

import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image,Long> {
    List<Image> findByBoard(Board board);
}
