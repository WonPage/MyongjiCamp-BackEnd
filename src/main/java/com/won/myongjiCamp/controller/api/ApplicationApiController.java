package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.auth.PrincipalDetail;
import com.won.myongjiCamp.model.application.Application;
import com.won.myongjiCamp.model.application.ApplicationFinalStatus;
import com.won.myongjiCamp.model.application.ApplicationStatus;
import com.won.myongjiCamp.model.board.CompleteBoard;
import com.won.myongjiCamp.model.board.RecruitBoard;
import com.won.myongjiCamp.model.board.RecruitStatus;
import com.won.myongjiCamp.model.board.role.Role;
import com.won.myongjiCamp.repository.ApplicationRepository;
import com.won.myongjiCamp.repository.MemberRepository;
import com.won.myongjiCamp.service.ApplicationService;
import com.won.myongjiCamp.dto.request.ApplicationDto;
import com.won.myongjiCamp.dto.response.ResponseDto;
import com.won.myongjiCamp.service.FcmService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ApplicationApiController {

    private final ApplicationService applicationService;
    private final MemberRepository memberRepository;
    private final ApplicationRepository applicationRepository;
    private final FcmService fcmService;

    //지원 (id = board id)
    @PostMapping("/api/auth/apply/{id}")
    public ResponseDto apply(@RequestBody @Valid ApplicationDto request, @PathVariable Long id, @AuthenticationPrincipal PrincipalDetail principal) throws IOException {
        applicationService.apply(request, id, principal.getMember());
        //모집자에게 지원서 도착 알림
        fcmService.applyNotification(id);
        return new ResponseDto(HttpStatus.OK.value(), "지원이 완료되었습니다.");
    }

    //지원 취소 (id = board id)
    @DeleteMapping("/api/auth/apply/{id}")
    public ResponseDto cancel(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetail principal) {
        applicationService.cancel(id, principal.getMember());
        return new ResponseDto(HttpStatus.OK.value(), "지원이 취소되었습니다.");
    }

    //first 지원 수락 or 거절 (id = application id) -> applicationstatus 수정
    //보낼 때 dto에 메세지 담아서 ~
    @PutMapping("/api/auth/apply/first/{id}")
    public ResponseDto firstResult(@RequestBody @Valid ApplicationDto request, @PathVariable Long id) throws IOException {
        applicationService.firstResult(request, id);
        //지원자에게 지원 결과 알림
        fcmService.firstResultNotification(id);
        return new ResponseDto(HttpStatus.OK.value(), "해당 지원 처리가 완료되었습니다.");
    }

    //first 지원 수락 or 거절 (id = application id) -> applicationstatus 수정
    //보낼 때 dto에 메세지 담아서 ~
    @PutMapping("/api/auth/apply/final/{id}")
    public ResponseDto finalResult(@RequestBody @Valid ApplicationDto request, @PathVariable Long id) throws IOException {
        applicationService.finalResult(request, id);
        //모집자에게 매칭 결과 알림
        fcmService.finalResultNotification(id);
        return new ResponseDto(HttpStatus.OK.value(), "해당 지원 처리가 완료되었습니다.");
    }
    
    //모집자 입장에서의 지원 확인 (id = 모집자 id) (지원현황)
    @GetMapping("/api/auth/apply/writer")
    public Result listOfWriter(@AuthenticationPrincipal PrincipalDetail principal) {
        List<RecruitBoard> boards = applicationService.listOfWriter(principal.getMember());
        List<ApplicationResponseDto> collect = boards.stream()
                .map(b -> new ApplicationResponseDto((CompleteBoard) b.getWriteCompleteBoard(), b.getId(), principal.getMember().getId(), applicationRepository.countByBoard(b), b.getTitle(), b.getStatus(), b.getCreatedDate()))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    //모집자 입장 지원서 목록 (id = 글 id)
    @GetMapping("/api/auth/apply/list/{id}")
    public Result listApplication(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetail principal){
        List<Application> applications = applicationService.listApplication(id);
        List<ApplicationResponseDto> collect = applications.stream()
                .map(b -> new ApplicationResponseDto(b.getId(), b.getApplicant().getNickname(), b.getApplicant().getProfileIcon(), b.getRole(), b.getFirstStatus(), b.getFinalStatus(), b.getCreatedDate()))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    //지원자 입장에서의 지원 확인 (id = 지원자 id) (지원현황)
    @GetMapping("/api/auth/apply/applicant")
    public Result listOfApplicant(@AuthenticationPrincipal PrincipalDetail principal) {
        List<Application> applications = applicationService.listOfApplicant(principal.getMember());
        List<ApplicationResponseDto> collect = applications.stream()
                .map(a -> new ApplicationResponseDto(a.getBoard().getId(), a.getId(), principal.getMember().getId(), a.getBoard().getTitle(), a.getFirstStatus(), a.getFinalStatus(), a.getCreatedDate()))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    //지원서 상세보기 (id = application id)
    @GetMapping("/api/auth/apply/detail/{id}")
    public Result detailApplication(@PathVariable Long id){
        Application application = applicationService.detailApplication(id);
        ApplicationResponseDto response = new ApplicationResponseDto(application.getId(),application.getApplicant().getNickname(), application.getApplicant().getProfileIcon(), application.getRole(), application.getUrl(),application.getContent(),application.getFirstStatus(),application.getFinalStatus());
        return new Result(response);
    }

    //지원 first 결과 메세지 (id = application id)
    @GetMapping("/api/auth/apply/result/{id}")
    public Result resultMessage(@PathVariable Long id) {
        Application application = applicationService.resultMessage(id);
        ApplicationResponseDto response = new ApplicationResponseDto(application.getId(),application.getResultContent(),application.getResultUrl(),application.getFirstStatus(),application.getFinalStatus());
        return new Result(response);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class ApplicationResponseDto {
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
        public ApplicationResponseDto(CompleteBoard completeBoard, Long boardId, Long memberId, Long num, String boardTitle, RecruitStatus recruitStatus, Timestamp boardcreatedDate) {
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
        public ApplicationResponseDto(Long boardId, Long applicationId, Long memberId, String boardTitle, ApplicationStatus firstStatus, ApplicationFinalStatus finalStatus, Timestamp applycreatedDate) {
            this.boardId = boardId;
            this.applicationId = applicationId;
            this.memberId = memberId;
            this.boardTitle = boardTitle;
            this.firstStatus = firstStatus;
            this.finalStatus = finalStatus;
            this.applycreatedDate = applycreatedDate;
        }

        //지원서 상세보기
        public ApplicationResponseDto(Long applicationId, String nickname, Integer icon, Role role, String applyUrl, String applyContent, ApplicationStatus firstStatus, ApplicationFinalStatus finalStatus) {
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
        public ApplicationResponseDto(Long applicationId, String nickname, Integer icon, Role role, ApplicationStatus firstStatus, ApplicationFinalStatus finalStatus, Timestamp applycreatedDate) {
            this.applicationId = applicationId;
            this.nickname = nickname;
            this.icon = icon;
            this.role = role;
            this.firstStatus = firstStatus;
            this.finalStatus = finalStatus;
            this.applycreatedDate = applycreatedDate;
        }

        //결과 메세지 확인
        public ApplicationResponseDto(Long applicationId, String resultContent, String resultUrl, ApplicationStatus firstStatus, ApplicationFinalStatus finalStatus) {
            this.applicationId = applicationId;
            this.resultContent = resultContent;
            this.resultUrl = resultUrl;
            this.firstStatus = firstStatus;
            this.finalStatus = finalStatus;
        }
    }

}
