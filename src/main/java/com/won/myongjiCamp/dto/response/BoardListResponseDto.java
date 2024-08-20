package com.won.myongjiCamp.dto.response;

import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.CompleteBoard;
import com.won.myongjiCamp.model.board.RecruitBoard;
import com.won.myongjiCamp.model.board.RecruitStatus;
import com.won.myongjiCamp.model.board.role.Role;
import com.won.myongjiCamp.model.board.role.RoleAssignment;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class BoardListResponseDto {
    private Long boardId;
    private String title;
    private Timestamp modifiedDate;
    private List<Role> roles;
    private String expectedDuration;
    private int commentCount;
    private int scrapCount;
    private String imageUrl;
    private RecruitStatus recruitStatus;
    private String boardType;

    public BoardListResponseDto(Board board) {
        this.boardId = board.getId();
        this.title = board.getTitle();
        this.modifiedDate = board.getModifiedDate();
        this.commentCount = board.getCommentCount();
        this.scrapCount = board.getScrapCount();

        if (board instanceof RecruitBoard recruitBoard) {
            this.roles = recruitBoard.getRoles().stream()
                    .map(RoleAssignment::getRole)
                    .collect(Collectors.toList());
            this.expectedDuration = recruitBoard.getExpectedDuration();
            this.recruitStatus = recruitBoard.getStatus();
            this.boardType = "recruit";
        }

        if (board instanceof CompleteBoard completeBoard) {
            this.imageUrl = completeBoard.getImages().get(0).getUrl();
            this.boardType = "complete";
        }
    }
}
