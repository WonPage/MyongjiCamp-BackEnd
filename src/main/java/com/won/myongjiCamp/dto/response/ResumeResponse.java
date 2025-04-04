package com.won.myongjiCamp.dto.response;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResumeResponse {
    private String title;
    private String content;
    private String url;
    private Timestamp createdDate;
    private Long id;

    public ResumeResponse(String title, Timestamp createdDate, Long id) {
        this.title = title;
        this.createdDate = createdDate;
        this.id = id;
    }
}
