package com.won.myongjiCamp.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Data
public class CommentDto {
// dto에서는 board 를 받고 객체 받기 x


    private Long id;

    private Long boardId;

    @NotEmpty
    @Length(max=300)
    private String content;

    private int cdepth;

//    @Column(nullable = true)
    private Long parentId;

    private List<CommentResponseDto> children = new ArrayList<>();

    private Integer isSecret;

    @Builder
    public CommentDto(Long id, Long boardId, String content,Integer isSecret){
        this.id = id;
        this.boardId = boardId;
        this.content = content;
        this.isSecret = isSecret;
    }

    @Builder
    public CommentDto(Long id, Long boardId, String content,Integer isSecret,Long parentId){ //childComment용
        this(id, boardId, content, isSecret);
        this.parentId = parentId;

    }


}
