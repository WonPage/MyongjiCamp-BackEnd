package com.won.myongjiCamp.repository;

import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.Scrap;
import com.won.myongjiCamp.model.board.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ScrapRepository extends JpaRepository<Scrap, Long>, JpaSpecificationExecutor<Scrap> {
    boolean existsByMemberAndBoard(Member member, Board board);

    void deleteByMemberAndBoard(Member member, Board board);

    @EntityGraph(attributePaths = "board")
    @Override
    Page<Scrap> findAll(Specification<Scrap> spec, Pageable pageable);
}
