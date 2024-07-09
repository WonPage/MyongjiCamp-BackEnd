package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.CommentDto;
import com.won.myongjiCamp.dto.CommentResponseDto;
import com.won.myongjiCamp.dto.ResponseDto;
import com.won.myongjiCamp.model.board.Comment;
import com.won.myongjiCamp.repository.CommentRepository;
import com.won.myongjiCamp.repository.MemberRepository;
import com.won.myongjiCamp.repository.RecruitRepository;
import com.won.myongjiCamp.service.CommentService;
import com.won.myongjiCamp.service.FcmService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;
    private final MemberRepository memberRepository;

    private final RecruitRepository recruitRepository;

    private final CommentRepository commentRepository;

    private final NotificationService notificationService;
    private final FcmService fcmService;

    //댓글 작성
    @PostMapping("/api/auth/recruit/{id}/comment")
        public ResponseDto<String> createComment(@RequestBody @Valid CommentDto commentDto, @AuthenticationPrincipal PrincipalDetail principal, @PathVariable Long id){
        commentService.create(commentDto,principal.getMember(),id);
//알림 써야 함
        //댓글 -> 게시글 작성자에게 알림
        fcmService.sendNotification(principal.getMember(), commentDto, id);
        //대댓 -> 댓글 작성자 & 게시글 작성자에게 알림

        return new ResponseDto<String>(HttpStatus.OK.value(), "댓글 작성 완료");
    }

    //댓글 작성 테스트용
/*    private final BoardRepository boardRepository;
    @PostMapping("/api/auth/recruit/{id}/comment")
    public ResponseDto<String> createComment(@RequestBody @Valid CommentDto commentDto, @PathVariable Long id) {
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
        Comment comment = commentService.create(commentDto, member, id);

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if(comment.getCdepth() == 0){
            System.out.println("0번 입니다.");
            CommentDto createCommentDto = convertCommentToDto(comment);
            notificationService.sendComment(board, createCommentDto); // 게시글 작성자에게 댓글 알람
        }
        else if(comment.getCdepth() == 1){
            System.out.println("1번 입니다.");

            CommentDto createChildCommentDto = convertChildCommentToDto(comment);
            notificationService.sendChildComment(board,createChildCommentDto);
        }

        return new ResponseDto<String>(HttpStatus.OK.value(), "댓글 작성 완료");
    }*/

    //댓글 삭제
    @DeleteMapping("/api/auth/recruit/{board_id}/comment/{comment_id}")
    public ResponseDto<String> deleteComment(@PathVariable("board_id") Long board_id, @PathVariable("comment_id") Long comment_id){
        commentService.delete(board_id, comment_id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "댓글이 삭제되었습니다.");
    }


    //댓글 전체 조회
    @GetMapping("/api/auth/recruit/{board_id}/comment")
    private Result CommentList(@PathVariable("board_id") Long id,@AuthenticationPrincipal PrincipalDetail principalDetail){

//        Board board = recruitRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
 /*       if (principalDetail == null) {
            return new Result<>(Collections.emptyList()); // 로그인하지 않은 경우 빈 리스트 반환
        }*/
        List<CommentResponseDto> result = new ArrayList<>();
        Map<Long, CommentResponseDto> map = new HashMap<>();
        List<Comment> commentList = commentService.commentAll(id);

        commentList.stream().forEach(c->{
            CommentResponseDto rDto = convertResponseCommentToDto(c);
            map.put(c.getId(), rDto);
            if(c.getCdepth() == 1){// 댓글이 부모가 있으면
                map.get(c.getParent().getId()).getChildren().add(rDto);
            }
            else{
                result.add(rDto);
            }

        });

        return new Result(result);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

/*
    public CommentDto convertCommentToDto(Comment comment){ // notification을 위한 아이
        return new CommentDto(
                comment.getId(),
                comment.getBoard().getId(),
                comment.getContent(),
                comment.getIsSecret()
                );

    }
    public CommentDto convertChildCommentToDto(Comment comment) { // notification을 위한 아이(원댓글의 id가 포함됨)
        return new CommentDto(
                comment.getId(),
                comment.getBoard().getId(),
                comment.getContent(),
                comment.getIsSecret(),
                comment.getParent().getId()
        );

    }
*/

    public CommentResponseDto convertResponseCommentToDto(Comment comment){
        return new CommentResponseDto(
                comment.getId(),
                comment.getBoard().getId(),
                comment.getContent(),
                comment.getCreatedDate(),
                comment.getWriter().getId(),
                comment.getWriter().getNickname(), 
                comment.getWriter().getProfileIcon(),
                comment.getIsSecret(),

                new ArrayList<>()
        );

    }



}


