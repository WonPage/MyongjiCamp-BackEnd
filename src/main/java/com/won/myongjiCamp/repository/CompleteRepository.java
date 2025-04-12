package com.won.myongjiCamp.repository;

import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.board.CompleteBoard;
import com.won.myongjiCamp.repository.custom.CompleteRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompleteRepository extends JpaRepository<CompleteBoard,Long>, CompleteRepositoryCustom {
    List findByMember(Member member);
}
