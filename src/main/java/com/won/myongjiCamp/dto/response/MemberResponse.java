package com.won.myongjiCamp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

public class MemberResponse {
    @Data
    @AllArgsConstructor
    public static class ProfileInformationResponseDto {
        private String email;
        private String nickname;
        private Integer profileIcon;
    }
}
