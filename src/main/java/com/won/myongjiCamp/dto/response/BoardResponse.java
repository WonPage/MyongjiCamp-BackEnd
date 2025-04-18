package com.won.myongjiCamp.dto.response;

import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.CompleteBoard;
import com.won.myongjiCamp.model.board.Image;
import com.won.myongjiCamp.model.board.RecruitBoard;
import com.won.myongjiCamp.model.board.RecruitStatus;
import com.won.myongjiCamp.model.board.role.Role;
import com.won.myongjiCamp.model.board.role.RoleAssignment;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;

public class BoardResponse {
    @Data
    public static class DetailRecruitResponseDto {
        private Long writerId;
        private String title;
        private String content;
        private Integer scrapCount;
        private RecruitStatus status;
        private String preferredLocation;
        private String expectedDuration;
        private List<RoleAssignmentDto> roleAssignments;
        private String nickname;
        private Integer profileIcon;
        private Timestamp modifiedDate;
        private Timestamp createdDate;
        private Long completeBoardId;

        public DetailRecruitResponseDto(RecruitBoard recruitBoard, List<RoleAssignmentDto> roleAssignmentDtoList) {
            this.writerId = recruitBoard.getMember().getId();
            this.title = recruitBoard.getTitle();
            this.content = recruitBoard.getContent();
            this.status = recruitBoard.getStatus();
            this.preferredLocation = recruitBoard.getPreferredLocation();
            this.expectedDuration = recruitBoard.getExpectedDuration();
            this.nickname = recruitBoard.getMember().getNickname();
            this.profileIcon = recruitBoard.getMember().getProfileIcon();
            this.modifiedDate = recruitBoard.getModifiedDate();
            this.createdDate = recruitBoard.getCreatedDate();
            this.roleAssignments = roleAssignmentDtoList;
            this.scrapCount = recruitBoard.getScrapCount();
            if (recruitBoard.getWriteCompleteBoard() != null) {
                this.completeBoardId = recruitBoard.getWriteCompleteBoard().getId();
            }
        }
    }

    @Data
    public static class NotDetailRecruitResponseDto {
        private Long writerId;
        private String title;
        private String content;
        private Integer scrapCount;
        private RecruitStatus status;
        private String preferredLocation;
        private String expectedDuration;
        private String nickname;
        private Integer profileIcon;
        private Timestamp modifiedDate;
        private Timestamp createdDate;
        private Long completeBoardId;

        public NotDetailRecruitResponseDto(RecruitBoard recruitBoard) {
            this.writerId = recruitBoard.getMember().getId();
            this.title = recruitBoard.getTitle();
            this.content = recruitBoard.getContent();
            this.status = recruitBoard.getStatus();
            this.preferredLocation = recruitBoard.getPreferredLocation();
            this.expectedDuration = recruitBoard.getExpectedDuration();
            this.nickname = recruitBoard.getMember().getNickname();
            this.profileIcon = recruitBoard.getMember().getProfileIcon();
            this.modifiedDate = recruitBoard.getModifiedDate();
            this.createdDate = recruitBoard.getCreatedDate();
            this.scrapCount = recruitBoard.getScrapCount();
            if (recruitBoard.getWriteCompleteBoard() != null) {
                this.completeBoardId = recruitBoard.getWriteCompleteBoard().getId();
            }
        }
    }

    @Data
    public static class CompleteBoardListResponseDto {
        private Long boardId;
        private String title;
        private Timestamp modifiedDate;
        private Timestamp createdDate;
        private int commentCount;
        private int scrapCount;
        private String firstImage;

        public CompleteBoardListResponseDto(CompleteBoard board) {
            this.boardId = board.getId();
            this.title = board.getTitle();
            this.modifiedDate = board.getModifiedDate();
            this.createdDate = board.getCreatedDate();
            this.commentCount = board.getCommentCount();
            this.scrapCount = board.getScrapCount();
            this.firstImage = board.getImages().get(0).getUrl();
        }
    }

    @Data
    public static class RecruitBoardListResponseDto {
        private Long boardId;
        private String title;
        private Timestamp modifiedDate;
        private Timestamp createdDate;
        private List<Role> roles;
        private String expectedDuration;
        private int commentCount;
        private int scrapCount;

        public RecruitBoardListResponseDto(RecruitBoard recruitBoard) {
            this.boardId = recruitBoard.getId();
            this.title = recruitBoard.getTitle();
            this.modifiedDate = recruitBoard.getModifiedDate();
            this.createdDate = recruitBoard.getCreatedDate();
            this.commentCount = recruitBoard.getCommentCount();
            this.scrapCount = recruitBoard.getScrapCount();
            this.roles = recruitBoard.getRoles().stream()
                    .map(RoleAssignment::getRole)
                    .collect(Collectors.toList());
            this.expectedDuration = recruitBoard.getExpectedDuration();
        }
    }

    @Data
    public static class DetailCompleteResponseDto {
        private Long writerId;
        private String title;
        private String content;
        private Integer scrapCount;
        private String nickname;
        private Integer profileIcon;
        private Timestamp createdDate;
        private List<String> imageUrls = new ArrayList<>();
        private Long recruitBoardId;

        public DetailCompleteResponseDto(CompleteBoard completeBoard) {
            this.writerId = completeBoard.getMember().getId();
            this.title = completeBoard.getTitle();
            this.content = completeBoard.getContent();
            this.nickname = completeBoard.getMember().getNickname();
            this.profileIcon = completeBoard.getMember().getProfileIcon();
            this.createdDate = completeBoard.getCreatedDate();
            this.scrapCount = completeBoard.getScrapCount();
            if (completeBoard.getWriteRecruitBoard() != null) {
                this.recruitBoardId = completeBoard.getWriteRecruitBoard().getId();
            }
            for (Image image : completeBoard.getImages()) {
                this.imageUrls.add(image.getUrl());
            }
        }
    }

    @Data
    public static class MemberCompleteBoardListResponseDto {
        private Long boardId;
        private String title;
        private Timestamp modifiedDate;
        private Timestamp createdDate;
        private int commentCount;
        private int scrapCount;

        public MemberCompleteBoardListResponseDto(Board board) {
            this.boardId = board.getId();
            this.title = board.getTitle();
            this.modifiedDate = board.getModifiedDate();
            this.createdDate = board.getCreatedDate();
            this.commentCount = board.getCommentCount();
            this.scrapCount = board.getScrapCount();
        }
    }

    @Data
    public static class RoleAssignmentDto {
        private Role role;
        private Integer requiredNumber;
        private Integer appliedNumber;

        public RoleAssignmentDto(Role role, Integer appliedNumber, Integer requiredNumber) {
            this.role = role;
            this.requiredNumber = requiredNumber;
            this.appliedNumber = appliedNumber;
        }
    }

    @Data
    @AllArgsConstructor
    public static class WriteCompleteResponseDto {
        private Long writerId;
        private Long boardId;
        private List<String> imageUrls;
    }
}
