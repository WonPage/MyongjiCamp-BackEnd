package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.security.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.response.ApplicationResponse;
import com.won.myongjiCamp.dto.response.ApplicationResponse.listOfApplicationsForBoardResponse;
import com.won.myongjiCamp.dto.response.ApplicationResponse.recruiterBoardListResponse;
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

    @PostMapping("/api/auth/apply/{boardId}")
    public ResponseDto apply(@RequestBody @Valid ApplicationRequest request, @PathVariable Long boardId,
                             @AuthenticationPrincipal PrincipalDetail principal) throws IOException {
        applicationService.apply(request, boardId, principal.getMember());
        fcmService.applyNotification(boardId);
        return new ResponseDto(HttpStatus.OK.value(), "지원이 완료되었습니다.");
    }

    @DeleteMapping("/api/auth/apply/{boardId}")
    public ResponseDto cancel(@PathVariable Long boardId, @AuthenticationPrincipal PrincipalDetail principal) {
        applicationService.cancel(boardId, principal.getMember());
        return new ResponseDto(HttpStatus.OK.value(), "지원이 취소되었습니다.");
    }

    @PutMapping("/api/auth/apply/first/{applicationId}")
    public ResponseDto processFirstResult(@RequestBody @Valid ApplicationRequest request, @PathVariable Long applicationId)
            throws IOException {
        applicationService.processFirstResult(request, applicationId);
        fcmService.firstResultNotification(applicationId);
        return new ResponseDto(HttpStatus.OK.value(), "해당 지원 처리가 완료되었습니다.");
    }

    @PutMapping("/api/auth/apply/final/{applicationId}")
    public ResponseDto processFinalResult(@RequestBody @Valid ApplicationRequest request, @PathVariable Long applicationId)
            throws IOException {
        applicationService.processFinalResult(request, applicationId);
        fcmService.finalResultNotification(applicationId);
        return new ResponseDto(HttpStatus.OK.value(), "해당 지원 처리가 완료되었습니다.");
    }

    @GetMapping("/api/auth/apply/writer")
    public Result getRecruiterBoardList(@AuthenticationPrincipal PrincipalDetail principal) {
        List<RecruitBoard> boards = applicationService.getRecruiterBoardList(principal.getMember());
        List<recruiterBoardListResponse> collect = boards.stream()
                .map(b -> new recruiterBoardListResponse((CompleteBoard) b.getWriteCompleteBoard(),
                        b.getId(), principal.getMember().getId(), applicationRepository.countByBoard(b), b.getTitle(),
                        b.getStatus(), b.getCreatedDate()))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    @GetMapping("/api/auth/apply/list/{boardId}")
    public Result getListOfApplicationsForBoard(@PathVariable Long boardId, @AuthenticationPrincipal PrincipalDetail principal) {
        List<Application> applications = applicationService.getListOfApplicationsForBoard(boardId, principal.getMember());
        List<listOfApplicationsForBoardResponse> collect = applications.stream()
                .map(b -> new listOfApplicationsForBoardResponse(b.getId(), b.getApplicant().getNickname(),
                        b.getApplicant().getProfileIcon(), b.getRole(), b.getFirstStatus(), b.getFinalStatus(),
                        b.getCreatedDate()))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    @GetMapping("/api/auth/apply/applicant")
    public Result getListOfApplicant(@AuthenticationPrincipal PrincipalDetail principal) {
        List<Application> applications = applicationService.getListOfApplicant(principal.getMember());
        List<ApplicationResponse.listOfApplicantResponse> collect = applications.stream()
                .map(a -> new ApplicationResponse.listOfApplicantResponse(a.getBoard().getId(), a.getId(),
                        principal.getMember().getId(),
                        a.getBoard().getTitle(), a.getFirstStatus(), a.getFinalStatus(), a.getCreatedDate()))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    @GetMapping("/api/auth/apply/detail/{applicationId}")
    public Result getDetailApplication(@PathVariable Long applicationId) {
        Application application = applicationService.getDetailApplication(applicationId);
        ApplicationResponse.detailResponse response = new ApplicationResponse.detailResponse(application.getId(),
                application.getApplicant().getNickname(), application.getApplicant().getProfileIcon(),
                application.getRole(), application.getUrl(), application.getContent(), application.getFirstStatus(),
                application.getFinalStatus());
        return new Result(response);
    }

    @GetMapping("/api/auth/apply/result/{applicationId}")
    public Result getResultMessage(@PathVariable Long applicationId) {
        Application application = applicationService.getResultMessage(applicationId);
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
