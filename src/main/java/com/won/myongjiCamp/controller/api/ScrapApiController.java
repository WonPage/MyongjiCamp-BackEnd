package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.ResponseDto;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.repository.MemberRepository;
import com.won.myongjiCamp.service.ScrapService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@RestController
@RequiredArgsConstructor
public class ScrapApiController {

    final private ScrapService scrapService;
    final private MemberRepository memberRepository;
//    @PostMapping("/api/auth/scrap/{id}")
//    public ResponseDto<String> scrap(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetail principal) {
//        String data = scrapService.scrap(id, principal.getMember());
//        return new ResponseDto<String>(HttpStatus.OK.value(), data);
//    }

    //스크랩 테스트
    @PostMapping("/api/auth/scrap/{id}")
    public ResponseDto<String> scrap(@PathVariable Long id) {
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
        String data = scrapService.scrap(id, member);
        return new ResponseDto<String>(HttpStatus.OK.value(), data);
    }
//    @Data
//    @AllArgsConstructor
//    static class Result<T> {
//        private T data;
//    }
//    @Data
//    @AllArgsConstructor
//    static class ResumeResponseDto {
//        private String title;
//        private String content;
//        private String url;
//        private Timestamp createDate;
//        private Long id;
//
//        public ResumeResponseDto(String title, Timestamp createDate, Long id) {
//            this.title = title;
//            this.createDate = createDate;
//            this.id = id;
//        }
//    }
}
