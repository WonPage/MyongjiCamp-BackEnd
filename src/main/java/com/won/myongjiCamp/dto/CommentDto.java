package com.won.myongjiCamp.dto;

import com.won.myongjiCamp.dto.request.CommentIdDto;
import com.won.myongjiCamp.model.Comment;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.RecruitBoard;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    private List<CommentIdDto> children = new ArrayList<>();


}
