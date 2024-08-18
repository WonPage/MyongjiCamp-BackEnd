package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.ReportDto;
import com.won.myongjiCamp.dto.ResponseDto;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.report.Report;
import com.won.myongjiCamp.model.board.report.ReportReason;
import com.won.myongjiCamp.model.board.report.ReportStatus;
import com.won.myongjiCamp.model.board.report.ReportTargetType;
import com.won.myongjiCamp.repository.MemberRepository;
import com.won.myongjiCamp.repository.ReportRepository;
import com.won.myongjiCamp.service.ReportService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReportApiController {
    private final MemberRepository memberRepository;
    private final ReportService reportService;
    private final ReportRepository reportRepository;

    // 신고 작성
    @PostMapping("/api/auth/report/{id}")
    public ResponseDto<String> createReport(@RequestBody @Valid ReportDto reportDto, @AuthenticationPrincipal PrincipalDetail principal, @PathVariable Long id){
        reportService.createReport(reportDto, principal.getMember(),id);

        return new ResponseDto<String>(HttpStatus.OK.value(), "신고 완료되었습니다.");
    }

}
