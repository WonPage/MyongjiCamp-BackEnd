package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.CommentDto;
import com.won.myongjiCamp.dto.RecruitDto;
import com.won.myongjiCamp.dto.ResponseDto;
import com.won.myongjiCamp.model.Comment;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.RecruitBoard;
import com.won.myongjiCamp.repository.CommentRepository;
import com.won.myongjiCamp.repository.MemberRepository;
import com.won.myongjiCamp.repository.RecruitRepository;
import com.won.myongjiCamp.service.CommentService;
import com.won.myongjiCamp.service.RecruitService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
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
public class CommentApiController {

    private final CommentService commentService;
    private final MemberRepository memberRepository;

    private final RecruitRepository recruitRepository;

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


    //대댓글 목록 ai

/*        private CommentRepository commentRepository;

        @GetMapping("/api/auth/recruit/{board_id}/comment/{comment_id}")
        public List<Comment> getChildren(@PathVariable("comment_id") Comment parentId) {
            return commentRepository.findByParentId(parentId);
        }*/

    //댓글 전체 조회
    @GetMapping("/api/auth/recruit/{board_id}/comment")
    private Result CommentList(@PathVariable("board_id") Long id){
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
        Board board = recruitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        Map<String, Object> map = new HashMap<>();
        List<Comment> findComment = commentService.commentAll(id);
        List<CommentResponseDto> commentList = findComment.stream()
                .map(m->new CommentResponseDto(m.getContent(),m.getCreateDate(),m.getWriter().getId()))
                .collect(Collectors.toList());
        return new Result(commentList);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
//    @AllArgsConstructor
    static class CommentResponseDto{
        private String content;
        private Timestamp commentCreateDate; //댓글 작성 시간
        private Long writerId;
        private List<CommentDto> children;

        public CommentResponseDto(String content, Timestamp commentCreateDate, Long writerId){
            this.content = content;
            this.commentCreateDate = commentCreateDate;
            this.writerId = writerId;
            this.children = children;
        }

    }

}


