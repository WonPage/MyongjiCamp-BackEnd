package com.won.myongjiCamp.repository.board.recruit;

import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.board.RecruitBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitRepository extends JpaRepository<RecruitBoard, Long>, RecruitRepositoryCustom {
    List<RecruitBoard> findByMember(Member writer);
}
