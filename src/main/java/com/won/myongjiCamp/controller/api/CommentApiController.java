package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.CommentDto;
import com.won.myongjiCamp.dto.RecruitDto;
import com.won.myongjiCamp.dto.ResponseDto;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.repository.MemberRepository;
import com.won.myongjiCamp.service.CommentService;
import com.won.myongjiCamp.service.RecruitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;
    private final MemberRepository memberRepository;

    //댓글 작성
/*    @PostMapping("/api/auth/recruit/{id}/comment")
    public ResponseDto<String> createComment(@RequestBody @Valid CommentDto commentDto, @AuthenticationPrincipal PrincipalDetail principal, @PathVariable Long id){
        commentService.create(commentDto,principal.getMember(),id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "댓글 작성 완료");
    }*/

    //댓글 작성 테스트용
    @PostMapping("/api/auth/recruit/{id}/comment")
    public ResponseDto<String> createComment(@RequestBody @Valid CommentDto commentDto, @PathVariable Long id) {
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
        commentService.create(commentDto, member, id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "댓글 작성 완료");
    }

    //댓글 삭제
    @DeleteMapping("/api/auth/recruit/{board_id}/comment/{comment_id}")
    public ResponseDto<String> deleteComment(@PathVariable("comment_id") Long comment_id){
        commentService.delete(comment_id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "댓글이 삭제되었습니다.");
    }


}


