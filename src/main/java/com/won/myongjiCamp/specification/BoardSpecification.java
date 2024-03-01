package com.won.myongjiCamp.specification;

import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.CompleteBoard;
import com.won.myongjiCamp.model.board.RecruitBoard;
import com.won.myongjiCamp.model.board.RecruitStatus;
import com.won.myongjiCamp.model.board.role.RoleAssignment;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class BoardSpecification {
//역할(태그)로 검색
    public static Specification<Board> withRoles(List<String> roles) {
        return (root, query, cb) -> {
            if (roles == null || roles.isEmpty()) {
                return null;
            }
            Join<Board, RoleAssignment> join = root.join("roles");
            return join.get("role").in(roles);
        };
    }
//제목이나 내용으로 검색
    public static Specification<Board> withTitleOrContent(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isEmpty()) {
                return null;
            }
            return cb.or(
                    cb.like(root.get("title"), "%" + keyword + "%"),
                    cb.like(root.get("content"), "%" + keyword + "%")
            );
        };
    }
//recruit 게시판 중 모집중(ongoing)인지, 모집완료(complete)인지
    public static Specification<Board> withStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isEmpty()) {
                return null;
            }

            if ("ongoing".equals(status)) {
                return cb.equal(root.get("status"), RecruitStatus.RECRUIT_ONGOING);
            }

            if ("complete".equals(status)) {
                return cb.equal(root.get("status"), RecruitStatus.RECRUIT_COMPLETE);
            }

            return null;
        };
    }
//recruit 게시판인지, complete 게시판인지
    public static Specification<Board> withBoardType(String boardType) {
        return (root, query, cb) -> {
            if (boardType == null || boardType.isEmpty()) {
                return null;
            }

            if ("recruit".equals(boardType)) {
                return cb.equal(root.type(), RecruitBoard.class);
            }

            if ("complete".equals(boardType)) {
                return cb.equal(root.type(), CompleteBoard.class);
            }

            return null;
        };
    }

}
