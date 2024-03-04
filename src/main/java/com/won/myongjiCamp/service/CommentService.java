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
    public void create(CommentDto commentDto, Member member, Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        if (commentDto.getCdepth() == 0) { //부모 댓글
            Comment comment = Comment.builder()
                    .board(board)
                    .content(commentDto.getContent())
                    .writer(member)
                    .cdepth(0)
                    .isDelete(false)
                    .build();
            commentRepository.save(comment);
        } else { // 대댓글
            Comment parentComment = commentRepository.findById(commentDto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
            Comment childComment = Comment.builder()
                    .board(board)
                    .parent(parentComment)
                    .content(commentDto.getContent())
                    .writer(member)
                    .cdepth(1)
                    .isDelete(false)
                    .build();
            commentRepository.save(childComment);


            List<Comment> child = parentComment.getChildren();
            child.add(childComment);
            parentComment.setChildren(child);

//            childComment childCo = childComment.builder()

//            CommentDto parentCommentDto = new CommentDto();
//            parentCommentDto.


//            parentComment.setChildren();
//            parentComment.getChildren().add(new CommentIdDto());
        }
        board.setCommentCount(board.getCommentCount() + 1);
    }


    // 댓글 삭제
    @Transactional
    public void delete(Long board_id, Long comment_id) {
        Comment comment = commentRepository.findById(comment_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재 하지 않습니다."));

        Board board = boardRepository.findById(board_id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if (comment.getCdepth() == 0) { // 부모 댓글
            if (comment.getChildren().size() == 0) { // 자식 x
                commentRepository.delete(comment);
            } else { // 자식 o
                comment.setDelete(true);
            }
        } else { // 자식 댓글
            commentRepository.delete(comment);
        }
        board.setCommentCount(board.getCommentCount() - 1);
    }

    // 댓글 조회
    public List<Comment> commentAll(Long id) { //여기서 id는 Board id
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        return commentRepository.findByBoard(board);

    }


}
