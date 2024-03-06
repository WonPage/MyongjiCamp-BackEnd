package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.ReportDto;
import com.won.myongjiCamp.dto.ResponseDto;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.repository.MemberRepository;
import com.won.myongjiCamp.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportApiController {
    private final MemberRepository memberRepository;
    private final ReportService reportService;
    // 신고 작성
/*    @PostMapping("/api/auth/report/{id}")
    public ResponseDto<String> createReport(@RequestBody @Valid ReportDto reportDto, @AuthenticationPrincipal PrincipalDetail principal, @PathVariable Long id){
        reportService.createReport(reportDto, principal.getMember(),id);

        return new ResponseDto<String>(HttpStatus.OK.value(), "신고 완료되었습니다.");
    }*/
 // 신고 작성 테스트 용
   @PostMapping("/api/auth/report/{id}")
    public ResponseDto<String> createReport(@RequestBody @Valid ReportDto reportDto, @PathVariable Long id){
        Member member = memberRepository.findById(2L)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
        reportService.createReport(reportDto, member,id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "신고 완료되었습니다.");

    }

}
