package com.won.myongjiCamp.repository;

import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.Scrap;
import com.won.myongjiCamp.model.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    boolean existsByMemberAndBoard(Member member, Board board);

    void deleteByMemberAndBoard(Member member, Board board);
}
