package com.won.myongjiCamp.dto.request;

import lombok.Data;

@Data
public class ScrapRequest {
    private int pageNum;
    private String boardType;
    private String status;
}
