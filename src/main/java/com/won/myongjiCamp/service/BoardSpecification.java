package com.won.myongjiCamp.service;

import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.CompleteBoard;
import com.won.myongjiCamp.model.board.RecruitBoard;
import com.won.myongjiCamp.model.board.RecruitStatus;
import com.won.myongjiCamp.model.board.role.RoleAssignment;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class BoardSpecification {

    public static Specification<Board> withRoles(List<String> roles) {
        return (root, query, cb) -> {
            if (roles == null || roles.isEmpty()) {
                return null;
            }
            Join<Board, RoleAssignment> join = root.join("roles");
            return join.get("role").in(roles);
        };
    }

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

    public static Specification<Board> withStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isEmpty()) {
                System.out.println("status가 null이야!");
                return null;
            }

            if ("ongoing".equals(status)) {
                System.out.println("status가 RECRUIT_ONGOING!");
                return cb.equal(root.get("status"), RecruitStatus.RECRUIT_ONGOING);
            }

            if ("complete".equals(status)) {
                System.out.println("status가 RECRUIT_COMPLETE!");
                return cb.equal(root.get("status"), RecruitStatus.RECRUIT_COMPLETE);
            }

            return null;
        };
    }

    public static Specification<Board> withBoardType(String boardType) {
        return (root, query, cb) -> {
            if (boardType == null || boardType.isEmpty()) {
                return null;
            }

            // boardType이 "recruit"일 경우 RecruitBoard만 검색
            if ("recruit".equals(boardType)) {
                return cb.equal(root.type(), RecruitBoard.class);
            }

            // boardType이 "recruit"일 경우 RecruitBoard만 검색
            if ("complete".equals(boardType)) {
                return cb.equal(root.type(), CompleteBoard.class);
            }

            // 그 외의 경우 null을 반환 (즉, 이 조건은 검색에 영향을 주지 않음)
            return null;
        };
    }
}
