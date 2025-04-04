package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.security.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.request.ReportRequest;
import com.won.myongjiCamp.dto.response.ResponseDto;
import com.won.myongjiCamp.repository.MemberRepository;
import com.won.myongjiCamp.repository.ReportRepository;
import com.won.myongjiCamp.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReportApiController {
    private final MemberRepository memberRepository;
    private final ReportService reportService;
    private final ReportRepository reportRepository;

    // 신고 작성
    @PostMapping("/api/auth/report/{id}")
    public ResponseDto<String> createReport(@RequestBody @Valid ReportRequest reportRequest, @AuthenticationPrincipal PrincipalDetail principal, @PathVariable Long id){
        reportService.createReport(reportRequest, principal.getMember(),id);

        return new ResponseDto<String>(HttpStatus.OK.value(), "신고 완료되었습니다.");
    }

}
