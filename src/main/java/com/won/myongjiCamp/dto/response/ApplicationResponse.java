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
    public static class listOfWriterResponse {
        private Long boardId; //글 id
        private Long memberId; //멤버 id
        private Long num; //이력서 개수
        private String boardTitle; //글 제목
        private Timestamp boardcreatedDate; //글 작성 시간
        private RecruitStatus recruitStatus;
        private Long completeBoardId;

        //모집자 입장에서의 지원 확인 (지원현황)
        public listOfWriterResponse(CompleteBoard completeBoard, Long boardId, Long memberId, Long num,
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
        private Long boardId; //글 id
        private Long applicationId; //지원 id
        private Long memberId; //멤버 id
        private String boardTitle; //글 제목
        private ApplicationStatus firstStatus; // 처음 요청 상태
        private ApplicationFinalStatus finalStatus; // 최종 요청 상태
        private Timestamp applycreatedDate; //지원한 시간
    }

    @Data
    @AllArgsConstructor
    public static class detailResponse {
        private Long applicationId; //지원 id
        private String nickname;
        private Integer icon;
        private Role role;
        private String applyUrl; //지원할때 적는 url
        private String applyContent;
        private ApplicationStatus firstStatus; // 처음 요청 상태
        private ApplicationFinalStatus finalStatus; // 최종 요청 상태
    }

    @Data
    @AllArgsConstructor
    public static class listOfApplicationsResponse {
        private Long applicationId; //지원 id
        private String nickname;
        private Integer icon;
        private Role role;
        private ApplicationStatus firstStatus; // 처음 요청 상태
        private ApplicationFinalStatus finalStatus; // 최종 요청 상태
        private Timestamp applycreatedDate; //지원한 시간
    }

    @Data
    @AllArgsConstructor
    public static class resultMessageResponse {
        private Long applicationId; //지원 id
        private String resultContent;
        private String resultUrl; //승인 or 거절 보낼때 적는 url
        private ApplicationStatus firstStatus; // 처음 요청 상태
        private ApplicationFinalStatus finalStatus; // 최종 요청 상태
    }

}
