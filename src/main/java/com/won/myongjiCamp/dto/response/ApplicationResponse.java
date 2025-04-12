package com.won.myongjiCamp.dto.response;

import com.won.myongjiCamp.model.application.ApplicationFinalStatus;
import com.won.myongjiCamp.model.application.ApplicationStatus;
import com.won.myongjiCamp.model.board.CompleteBoard;
import com.won.myongjiCamp.model.board.RecruitStatus;
import com.won.myongjiCamp.model.board.role.Role;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;

public class ApplicationResponse {
    @Data
    public static class recruiterBoardListResponse {
        private Long boardId;
        private Long memberId;
        private Long num; //이력서 개수
        private String boardTitle;
        private Timestamp boardcreatedDate;
        private RecruitStatus recruitStatus;
        private Long completeBoardId;

        public recruiterBoardListResponse(CompleteBoard completeBoard, Long boardId, Long memberId, Long num,
                                          String boardTitle, RecruitStatus recruitStatus, Timestamp boardcreatedDate) {
            this.boardId = boardId;
            this.memberId = memberId;
            this.num = num;
            this.boardTitle = boardTitle;
            this.boardcreatedDate = boardcreatedDate;
            this.recruitStatus = recruitStatus;
            if (completeBoard != null) {
                this.completeBoardId = completeBoard.getId();
            }
        }
    }

    @Data
    @AllArgsConstructor
    public static class listOfApplicantResponse {
        private Long boardId;
        private Long applicationId;
        private Long memberId;
        private String boardTitle;
        private ApplicationStatus firstStatus;
        private ApplicationFinalStatus finalStatus;
        private Timestamp applycreatedDate;
    }

    @Data
    @AllArgsConstructor
    public static class detailResponse {
        private Long applicationId;
        private String nickname;
        private Integer icon;
        private Role role;
        private String applyUrl;
        private String applyContent;
        private ApplicationStatus firstStatus;
        private ApplicationFinalStatus finalStatus;
    }

    @Data
    @AllArgsConstructor
    public static class listOfApplicationsForBoardResponse {
        private Long applicationId;
        private String nickname;
        private Integer icon;
        private Role role;
        private ApplicationStatus firstStatus;
        private ApplicationFinalStatus finalStatus;
        private Timestamp applycreatedDate;
    }

    @Data
    @AllArgsConstructor
    public static class resultMessageResponse {
        private Long applicationId;
        private String resultContent;
        private String resultUrl;
        private ApplicationStatus firstStatus;
        private ApplicationFinalStatus finalStatus;
    }
}
