package com.won.myongjiCamp.dto.Fcm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FcmSendDto { //client에서 전달받은 객체
    private String token;
    private String title;
    private String body;
}
