package com.won.myongjiCamp.dto.request;

import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.List;

//검색할 때의 requestDto
@Data
public class BoardSearchDto {
    private List<String> roles;
    private String keyword;
    private int pageNum;
    private Sort.Direction direction = Sort.Direction.DESC;
    private String boardType;
    private String status;
}
