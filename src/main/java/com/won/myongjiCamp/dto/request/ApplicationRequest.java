package com.won.myongjiCamp.dto.request;

import lombok.Data;

@Data
public class ApplicationRequest {

    private String role;

    private String content;

    private String url;

    private String firstStatus;

    private String finalStatus;

    private String resultContent;

    private String resultUrl;
}
