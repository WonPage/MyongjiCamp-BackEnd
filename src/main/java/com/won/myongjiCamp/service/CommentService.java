package com.won.myongjiCamp.service;

import com.won.myongjiCamp.dto.CommentDto;
import com.won.myongjiCamp.model.Comment;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.repository.BoardRepository;
import com.won.myongjiCamp.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    // 댓글 작성
    @Transactional
    public void create(CommentDto commentDto, Member member, Long id){
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        if(commentDto.getCDepth() == 0){ //부모 댓글
            Comment comment = Comment.builder()
                    .board(board)
                    .content(commentDto.getContent())
                    .writer(member)
                    .cDepth(0)
                    .isDelete(false)
                    .children(commentDto.getChildren())
                    .build();
            commentRepository.save(comment);
        }
        else{ // 대댓글
            Comment comment = commentRepository.findById(id)
                    .orElseThrow(()->new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        }

    // 대댓글이면 댓글 child에 값 추가
    }

/*    // 댓글 조회
    public List<Comment> commentAll(Board board){
        // 정렬 어떻게 하냐..
        return commentRepository.findByBoard(board);
    }*/

    // 댓글 삭제
    @Transactional
    public void delete(CommentDto commentDto, Member member, Long id){
/*        Board board = boardRepository.findById(commentDto.getBoardId())
                .orElseThrow(()->new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));*/
        Comment comment = commentRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("해당 댓글이 존재 하지 않습니다."));
        // 부모 댓글임
        // 1. 자식 댓글이 없음 -> 그냥 삭제
        // 2. 자식 댓글이 있음 -> 내용만 삭제 ("삭제된 내용입니다.")
/*        if(comment.getCDepth() == 0 && ){
        }*/


        //자식 댓글임
        // 그냥 삭제



    }


}
