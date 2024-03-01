package com.won.myongjiCamp.dto;

import com.won.myongjiCamp.model.Comment;
import com.won.myongjiCamp.repository.CommentRepository;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;


@Data
public class CommentDto {
// dto에서는 board 를 받고 객체 받기 x
//    private Long id;

//    private Long boardId;

    @NotEmpty
    @Length(max=300)
    private String content;

    private int cdepth;

    private Long parentId;

    private List<CommentResponseDto> children = new ArrayList<>();

/*
    public CommentDto(Comment comment){
        this.content = comment.getContent();
        this.cdepth = comment.getCdepth();
        this.parentId = comment.getParent().getId();
        this.children = comment.getChildren();
    }
    private List<CommentResponseDto> convertChildrenToDto(List<Comment> children) {
        List<CommentResponseDto> dtos = new ArrayList<>();
        for (Comment child : children) {
            dtos.add(new CommentResponseDto(child)); // Convert each child to CommentResponseDto
        }
        return dtos;
    }
*/



}
