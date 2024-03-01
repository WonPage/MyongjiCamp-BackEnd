package com.won.myongjiCamp.specification;

import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.Scrap;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.CompleteBoard;
import com.won.myongjiCamp.model.board.RecruitBoard;
import com.won.myongjiCamp.model.board.RecruitStatus;
import org.springframework.data.jpa.domain.Specification;

public class ScrapSpecification {

    //recruit 게시판 중 모집중(ongoing)인지, 모집완료(complete)인지
    public static Specification<Scrap> withStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isEmpty()) {
                return null;
            }

            if ("ongoing".equals(status)) {
                return cb.equal(root.get("board").get("status"), RecruitStatus.RECRUIT_ONGOING);
            }

            if ("complete".equals(status)) {
                return cb.equal(root.get("board").get("status"), RecruitStatus.RECRUIT_COMPLETE);
            }

            return null;
        };
    }
    //recruit 게시판인지, complete 게시판인지
    public static Specification<Scrap> withBoardType(String boardType) {
        return (root, query, cb) -> {
            if (boardType == null || boardType.isEmpty()) {
                return null;
            }

            if ("recruit".equals(boardType)) {
                return cb.equal(root.get("board").type(), RecruitBoard.class);
            }

            if ("complete".equals(boardType)) {
                return cb.equal(root.get("board").type(), CompleteBoard.class);
            }

            return null;
        };
    }
    //멤버로 찾기
    public static Specification<Scrap> withMember(Member member) {
        return (root, query, cb) -> cb.equal(root.get("member"), member);
    }

}
