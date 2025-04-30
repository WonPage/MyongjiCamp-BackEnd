package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.security.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.response.ResponseDto;
import com.won.myongjiCamp.dto.request.ResumeRequest;
import com.won.myongjiCamp.dto.response.ResumeResponse;
import com.won.myongjiCamp.exception.MemberNoMatchException;
import com.won.myongjiCamp.model.Resume;
import com.won.myongjiCamp.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ResumeApiController {

    private final ResumeService resumeService;

    @PostMapping("/api/auth/resume")
    public ResponseDto<String> writeResume(@RequestBody @Valid ResumeRequest request, @AuthenticationPrincipal PrincipalDetail principal) {
        resumeService.write(request.getTitle(), request.getContent(), request.getUrl(), principal.getMember());
        return new ResponseDto<String>(HttpStatus.OK.value(), "이력서 작성 완료");
    }

    @PutMapping("/api/auth/resume/{id}")
    public ResponseDto<String> updateResume(@RequestBody @Valid ResumeRequest request, @PathVariable long id) {
        resumeService.update(request.getTitle(),request.getContent(),request.getUrl(), id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "이력서 수정 완료");
    }

    @DeleteMapping("/api/auth/resume/{id}")
    public ResponseDto<String> deleteResume(@PathVariable long id) {
        resumeService.delete(id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "이력서 삭제 완료");
    }

    @GetMapping("/api/auth/resume")
    public ResponseDto<List<ResumeResponse>> getListResume(@AuthenticationPrincipal PrincipalDetail principal) {
        List<Resume> findResume = resumeService.getListResume(principal.getMember());
        List<ResumeResponse> collect = findResume.stream()
                .map(m -> new ResumeResponse(m.getTitle(), m.getCreatedDate(), m.getId()))
                .collect(Collectors.toList());

        return new ResponseDto<>(HttpStatus.OK.value(), collect);
    }

    @GetMapping("/api/auth/resume/{id}")
    public ResponseDto<ResumeResponse> getDetailResume(@PathVariable long id, @AuthenticationPrincipal PrincipalDetail principal) throws MemberNoMatchException {
        Resume resume = resumeService.getDetailResume(id, principal.getMember());
        ResumeResponse response =  new ResumeResponse(resume.getTitle(), resume.getContent(), resume.getUrl(), resume.getCreatedDate(), resume.getId());

        return new ResponseDto<>(HttpStatus.OK.value(), response);
    }
}
