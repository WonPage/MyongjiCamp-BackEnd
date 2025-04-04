package com.won.myongjiCamp.dto.response;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class CommentResponse { //댓글 조회할 때
    private Long id; //댓글 id
    private Long boardId; //게시글 id
    private String content;
    private Timestamp commentCreateDate; //댓글 작성 시간
    private Long writerId; // 작성자 id
    private List<CommentResponse> children;

    private String nickname;

    private Integer profileIcon;

    private Integer isSecret;

    private boolean isDelete;

    public CommentResponse(Long id, Long boardId, String content, Timestamp commentCreateDate, Long writerId, String nickname, Integer profileIcon, Integer isSecret, List<CommentResponse> children, boolean isDelete){
        this.id=id;
        this.boardId = boardId;
        this.content = content;
        this.commentCreateDate = commentCreateDate;
        this.writerId = writerId;
        this.nickname = nickname;
        this.profileIcon = profileIcon;
        this.isSecret = isSecret;
        this.children = children;
        this.isDelete = isDelete;
    }
}