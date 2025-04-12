package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.security.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.response.ApplicationResponse;
import com.won.myongjiCamp.model.application.Application;
import com.won.myongjiCamp.model.board.CompleteBoard;
import com.won.myongjiCamp.model.board.RecruitBoard;
import com.won.myongjiCamp.repository.ApplicationRepository;
import com.won.myongjiCamp.service.ApplicationService;
import com.won.myongjiCamp.dto.request.ApplicationRequest;
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
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ApplicationApiController {

    private final ApplicationService applicationService;
    private final ApplicationRepository applicationRepository;
    private final FcmService fcmService;

    //지원 (id = board id)
    @PostMapping("/api/auth/apply/{id}")
    public ResponseDto apply(@RequestBody @Valid ApplicationRequest request, @PathVariable Long id,
                             @AuthenticationPrincipal PrincipalDetail principal) throws IOException {
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
    public ResponseDto firstResult(@RequestBody @Valid ApplicationRequest request, @PathVariable Long id)
            throws IOException {
        applicationService.firstResult(request, id);
        //지원자에게 지원 결과 알림
        fcmService.firstResultNotification(id);
        return new ResponseDto(HttpStatus.OK.value(), "해당 지원 처리가 완료되었습니다.");
    }

    //first 지원 수락 or 거절 (id = application id) -> applicationstatus 수정
    //보낼 때 dto에 메세지 담아서 ~
    @PutMapping("/api/auth/apply/final/{id}")
    public ResponseDto finalResult(@RequestBody @Valid ApplicationRequest request, @PathVariable Long id)
            throws IOException {
        applicationService.finalResult(request, id);
        //모집자에게 매칭 결과 알림
        fcmService.finalResultNotification(id);
        return new ResponseDto(HttpStatus.OK.value(), "해당 지원 처리가 완료되었습니다.");
    }

    //모집자 입장에서의 지원 확인 (id = 모집자 id) (지원현황)
    @GetMapping("/api/auth/apply/writer")
    public Result listOfWriter(@AuthenticationPrincipal PrincipalDetail principal) {
        List<RecruitBoard> boards = applicationService.listOfWriter(principal.getMember());
        List<ApplicationResponse.listOfWriterResponse> collect = boards.stream()
                .map(b -> new ApplicationResponse.listOfWriterResponse((CompleteBoard) b.getWriteCompleteBoard(),
                        b.getId(), principal.getMember().getId(), applicationRepository.countByBoard(b), b.getTitle(),
                        b.getStatus(), b.getCreatedDate()))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    //모집자 입장 지원서 목록 (id = 글 id) -> fetchJoin 적용
    @GetMapping("/api/auth/apply/list/{id}")
    public Result listOfApplications(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetail principal) {
        List<Application> applications = applicationService.listApplication(id, principal.getMember());
        List<ApplicationResponse.listOfApplicationsResponse> collect = applications.stream()
                .map(b -> new ApplicationResponse.listOfApplicationsResponse(b.getId(), b.getApplicant().getNickname(),
                        b.getApplicant().getProfileIcon(), b.getRole(), b.getFirstStatus(), b.getFinalStatus(),
                        b.getCreatedDate()))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    //지원자 입장에서의 지원 확인 (id = 지원자 id) (지원현황)
    @GetMapping("/api/auth/apply/applicant")
    public Result listOfApplicant(@AuthenticationPrincipal PrincipalDetail principal) {
        List<Application> applications = applicationService.listOfApplicant(principal.getMember());
        List<ApplicationResponse.listOfApplicantResponse> collect = applications.stream()
                .map(a -> new ApplicationResponse.listOfApplicantResponse(a.getBoard().getId(), a.getId(),
                        principal.getMember().getId(),
                        a.getBoard().getTitle(), a.getFirstStatus(), a.getFinalStatus(), a.getCreatedDate()))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    //지원서 상세보기 (id = application id)
    @GetMapping("/api/auth/apply/detail/{id}")
    public Result detailApplication(@PathVariable Long id) {
        Application application = applicationService.detailApplication(id);
        ApplicationResponse.detailResponse response = new ApplicationResponse.detailResponse(application.getId(),
                application.getApplicant().getNickname(), application.getApplicant().getProfileIcon(),
                application.getRole(), application.getUrl(), application.getContent(), application.getFirstStatus(),
                application.getFinalStatus());
        return new Result(response);
    }

    //지원 first 결과 메세지 (id = application id)
    @GetMapping("/api/auth/apply/result/{id}")
    public Result resultMessage(@PathVariable Long id) {
        Application application = applicationService.resultMessage(id);
        ApplicationResponse.resultMessageResponse response = new ApplicationResponse.resultMessageResponse(
                application.getId(), application.getResultContent(),
                application.getResultUrl(), application.getFirstStatus(), application.getFinalStatus());
        return new Result(response);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
