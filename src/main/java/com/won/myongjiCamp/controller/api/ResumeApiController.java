package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.response.ResponseDto;
import com.won.myongjiCamp.dto.request.ResumeDto;
import com.won.myongjiCamp.exception.MemberNoMatchException;
import com.won.myongjiCamp.model.Resume;
import com.won.myongjiCamp.service.ResumeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ResumeApiController {

    private final ResumeService resumeService;

    @PostMapping("/api/auth/resume") // 이력서 작성
    public ResponseDto<String> writeResume(@RequestBody @Valid ResumeDto request, @AuthenticationPrincipal PrincipalDetail principal) {
        resumeService.write(request.getTitle(), request.getContent(), request.getUrl(), principal.getMember());
        return new ResponseDto<String>(HttpStatus.OK.value(), "이력서 작성 완료");
    }

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
    //이력서 전체 조회
    @GetMapping("/api/auth/resume")
        public Result ListResume(@AuthenticationPrincipal PrincipalDetail principal) {
        Map<String, Object> map = new HashMap<>();
        List<Resume> findResume = resumeService.resumeAll(principal.getMember());
        List<ResumeResponseDto> collect = findResume.stream()
                .map(m -> new ResumeResponseDto(m.getTitle(), m.getCreatedDate(), m.getId()))
                .collect(Collectors.toList());
        return new Result(collect);
    }
    //이력서 상세보기
    @GetMapping("/api/auth/resume/{id}")
    public Result ListResume(@PathVariable long id, @AuthenticationPrincipal PrincipalDetail principal) throws MemberNoMatchException {
        Map<String, Object> map = new HashMap<>();
        Resume resume = resumeService.resumeDetail(id, principal.getMember());
        ResumeResponseDto response =  new ResumeResponseDto(resume.getTitle(), resume.getContent(), resume.getUrl(), resume.getCreatedDate(), resume.getId());
        return new Result(response);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
    @Data
    @AllArgsConstructor
    static class ResumeResponseDto {
        private String title;
        private String content;
        private String url;
        private Timestamp createdDate;
        private Long id;

        public ResumeResponseDto(String title, Timestamp createdDate, Long id) {
            this.title = title;
            this.createdDate = createdDate;
            this.id = id;
        }
    }


}
