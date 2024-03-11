package com.won.myongjiCamp.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
//    @AllArgsConstructor
public class CommentResponseDto{ //댓글 조회할 때
    private Long id; //댓글 id
    private Long boardId; //게시글 id
    private String content;
    private Timestamp commentCreateDate; //댓글 작성 시간
    private Long writerId; // 작성자 id
    private List<CommentResponseDto> children;

    private String nickname;

    private Integer profileIcon;


    public CommentResponseDto(Long id,Long boardId,String content, Timestamp commentCreateDate, Long writerId,String nickname,Integer profileIcon,List<CommentResponseDto> children){

        this.id=id;
        this.boardId = boardId;
        this.content = content;
        this.commentCreateDate = commentCreateDate;
        this.writerId = writerId;
        this.nickname = nickname;
        this.profileIcon = profileIcon;
        this.children = children;

    }



}