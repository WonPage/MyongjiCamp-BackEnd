package com.won.myongjiCamp.repository.board.complete;

import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.board.CompleteBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompleteRepository extends JpaRepository<CompleteBoard,Long>, CompleteRepositoryCustom {
    List findByMember(Member member);
}
