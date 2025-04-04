package com.won.myongjiCamp.dto.response;

import com.won.myongjiCamp.model.application.ApplicationFinalStatus;
import com.won.myongjiCamp.model.application.ApplicationStatus;
import com.won.myongjiCamp.model.board.CompleteBoard;
import com.won.myongjiCamp.model.board.RecruitStatus;
import com.won.myongjiCamp.model.board.role.Role;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplicationResponse {
    private Long boardId; //글 id
    private Long applicationId; //지원 id
    private Long memberId; //멤버 id
    private Long num; //이력서 개수
    private String boardTitle; //글 제목
    private RecruitStatus recruitStatus;
    private String applyUrl; //지원할때 적는 url
    private String resultUrl; //승인 or 거절 보낼때 적는 url
    private ApplicationStatus firstStatus; // 처음 요청 상태
    private ApplicationFinalStatus finalStatus; // 최종 요청 상태
    private Timestamp applycreatedDate; //지원한 시간
    private Timestamp boardcreatedDate; //글 작성 시간
    private Role role;
    private Integer icon;
    private String nickname;
    private String applyContent;
    private String resultContent;
    private Long completeBoardId;

    //모집자 입장에서의 지원 확인 (지원현황)
    public ApplicationResponse(CompleteBoard completeBoard, Long boardId, Long memberId, Long num, String boardTitle, RecruitStatus recruitStatus, Timestamp boardcreatedDate) {
        this.boardId = boardId;
        this.memberId = memberId;
        this.num = num;
        this.boardTitle = boardTitle;
        this.boardcreatedDate = boardcreatedDate;
        this.recruitStatus = recruitStatus;
        if (completeBoard != null){
            this.completeBoardId = completeBoard.getId();
        }
    }

    //지원자 입장에서의 지원 확인 (지원현황)
    public ApplicationResponse(Long boardId, Long applicationId, Long memberId, String boardTitle, ApplicationStatus firstStatus, ApplicationFinalStatus finalStatus, Timestamp applycreatedDate) {
        this.boardId = boardId;
        this.applicationId = applicationId;
        this.memberId = memberId;
        this.boardTitle = boardTitle;
        this.firstStatus = firstStatus;
        this.finalStatus = finalStatus;
        this.applycreatedDate = applycreatedDate;
    }

    //지원서 상세보기
    public ApplicationResponse(Long applicationId, String nickname, Integer icon, Role role, String applyUrl, String applyContent, ApplicationStatus firstStatus, ApplicationFinalStatus finalStatus) {
        this.applicationId = applicationId;
        this.nickname = nickname;
        this.icon = icon;
        this.role = role;
        this.applyUrl = applyUrl;
        this.applyContent = applyContent;
        this.firstStatus = firstStatus;
        this.finalStatus = finalStatus;
    }

    //지원서 목록
    public ApplicationResponse(Long applicationId, String nickname, Integer icon, Role role, ApplicationStatus firstStatus, ApplicationFinalStatus finalStatus, Timestamp applycreatedDate) {
        this.applicationId = applicationId;
        this.nickname = nickname;
        this.icon = icon;
        this.role = role;
        this.firstStatus = firstStatus;
        this.finalStatus = finalStatus;
        this.applycreatedDate = applycreatedDate;
    }

    //결과 메세지 확인
    public ApplicationResponse(Long applicationId, String resultContent, String resultUrl, ApplicationStatus firstStatus, ApplicationFinalStatus finalStatus) {
        this.applicationId = applicationId;
        this.resultContent = resultContent;
        this.resultUrl = resultUrl;
        this.firstStatus = firstStatus;
        this.finalStatus = finalStatus;
    }
}
