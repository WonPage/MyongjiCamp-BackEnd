package com.won.myongjiCamp.dto;

import com.won.myongjiCamp.model.Comment;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public
//    @AllArgsConstructor
class CommentResponseDto{
    private String content;
    private Timestamp commentCreateDate; //댓글 작성 시간
    private Long writerId;
    private List<CommentResponseDto> children;








    public CommentResponseDto(String content, Timestamp commentCreateDate, Long writerId,List<CommentResponseDto> children){
        // 내 id 필요?
        this.content = content;
        this.commentCreateDate = commentCreateDate;
        this.writerId = writerId;
        this.commentCreateDate = commentCreateDate;
        this.children = children;


    }



}