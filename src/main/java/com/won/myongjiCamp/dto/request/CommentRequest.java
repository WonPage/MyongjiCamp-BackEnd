package com.won.myongjiCamp.dto.request;

import com.won.myongjiCamp.dto.response.CommentResponse;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Data
public class CommentRequest {
    private Long id;
    private Long boardId;

    @NotEmpty
    @Length(max=300)
    private String content;

    private int cdepth;
    private Long parentId;
    private List<CommentResponse> children = new ArrayList<>();
    private Integer isSecret;

    @Builder
    public CommentRequest(Long id, Long boardId, String content, Integer isSecret){
        this.id = id;
        this.boardId = boardId;
        this.content = content;
        this.isSecret = isSecret;
    }

    @Builder
    public CommentRequest(Long id, Long boardId, String content, Integer isSecret, Long parentId){ //childComment용
        this(id, boardId, content, isSecret);
        this.parentId = parentId;

    }
}
