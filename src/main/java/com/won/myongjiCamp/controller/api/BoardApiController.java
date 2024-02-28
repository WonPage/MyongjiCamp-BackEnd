package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.RecruitDto;
import com.won.myongjiCamp.dto.ResponseDto;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.repository.MemberRepository;
import com.won.myongjiCamp.service.RecruitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardApiController {

    private final RecruitService recruitService;

    private final MemberRepository memberRepository;
/*    @PostMapping("/api/recruit")
    public ResponseDto<String> createRecruit(@RequestBody @Valid RecruitDto recruitDto,@AuthenticationPrincipal PrincipalDetail principal){
        recruitService.create(recruitDto,principal.getMember());
        return new ResponseDto<String>(HttpStatus.OK.value(),"게시글 작성 완료");
    }*/
    //테스트 코드
    @PostMapping("/api/auth/recruit")
    public ResponseDto<String> createRecruit(@RequestBody @Valid RecruitDto recruitDto){
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
        recruitService.create(recruitDto,member);
        return new ResponseDto<String>(HttpStatus.OK.value(),"게시글 작성 완료");
    }

    @PutMapping("/api/auth/recruit/{id}")
    public ResponseDto<String> updateRecruit(@RequestBody @Valid RecruitDto recruitDto, @PathVariable long id){
        recruitService.update(recruitDto,id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "게시글이 수정되었습니다.");
    }
    
    @DeleteMapping("/api/auth/recruit/{id}")
    public ResponseDto<String> deleteRecruit(@PathVariable long id){
        recruitService.delete(id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "게시글이 삭제되었습니다.");
    }



}
