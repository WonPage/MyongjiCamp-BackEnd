package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.RecruitDto;
import com.won.myongjiCamp.dto.ResponseDto;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.repository.MemberRepository;
import com.won.myongjiCamp.service.RecruitService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardApiController {

    private final RecruitService recruitService;

    private final MemberRepository memberRepository;
    // 게시글 작성
/*
    @PostMapping("/api/auth/recruit")
    public ResponseDto<String> createRecruit(@RequestBody @Valid RecruitDto recruitDto,@AuthenticationPrincipal PrincipalDetail principal){
        recruitService.create(recruitDto,principal.getMember());
        return new ResponseDto<String>(HttpStatus.OK.value(),"게시글 작성 완료");
    }
*/

    // 게시글 작성 테스트용
    @PostMapping("/api/auth/recruit")
    public ResponseDto<String> createRecruit(@RequestBody @Valid RecruitDto recruitDto){
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
        recruitService.create(recruitDto,member);
        return new ResponseDto<String>(HttpStatus.OK.value(),"게시글 작성 완료");
    }
    // 게시글 수정, id는 게시글 id
    @PutMapping("/api/auth/recruit/{id}")
    public ResponseDto<String> updateRecruit(@RequestBody @Valid RecruitDto recruitDto, @PathVariable long id){
        recruitService.update(recruitDto,id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "게시글이 수정되었습니다.");
    }

    // 게시글 삭제, id는 게시글 id
    @DeleteMapping("/api/auth/recruit/{id}")
    public ResponseDto<String> deleteRecruit(@PathVariable long id){
        recruitService.delete(id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "게시글이 삭제되었습니다.");
    }

    // 게시글 상세 읽기
/*    @GetMapping("/api/auth/recruit/{id}")
    public */

    //get할 때는 그냥 Dto로 해주는 것보다는 Result에 담아서 주는 것이 좋다.
    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

/*    @Data
    @AllArgsConstructor
    static class RecruitResPonseDto {
        private String title;
        private String content;
    }*/

}
