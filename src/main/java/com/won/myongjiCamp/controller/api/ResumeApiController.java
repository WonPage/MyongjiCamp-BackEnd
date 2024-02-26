package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.ResponseDto;
import com.won.myongjiCamp.dto.ResumeDto;
import com.won.myongjiCamp.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ResumeApiController {

    private final ResumeService resumeService;

    @PostMapping("/api/auth/resume") // 이력서 작성
    public ResponseDto<String> writeResume(@RequestBody @Valid ResumeDto request, @AuthenticationPrincipal PrincipalDetail principal) {
        resumeService.write(request.getTitle(),request.getContent(),request.getUrl(), principal.getMember());
        return new ResponseDto<String>(HttpStatus.OK.value(), "이력서 작성 완료");
    }

//    @PostMapping("/api/auth/resume") // 이력서 작성 테스트
//    public ResponseDto<String> writeResume(@RequestBody @Valid ResumeDto request) {
//
//        Member member = memberRepository.findById(1L)
//                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
//        resumeService.write(request.getTitle(),request.getContent(),request.getUrl(), member);
//        return new ResponseDto<String>(HttpStatus.OK.value(), "이력서 작성 완료");
//    }

    @PutMapping("/api/auth/resume/{id}") // 이력서 수정, id는 이력서 id
    public ResponseDto<String> updateResume(@RequestBody @Valid ResumeDto request, @PathVariable long id) {
        resumeService.update(request.getTitle(),request.getContent(),request.getUrl(), id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "이력서 수정 완료");
    }

    @DeleteMapping("/api/auth/resume/{id}") // 이력서 삭제, id는 이력서 id
    public ResponseDto<String> deleteResume(@PathVariable long id) {
        resumeService.delete(id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "이력서 삭제 완료");
    }
}
