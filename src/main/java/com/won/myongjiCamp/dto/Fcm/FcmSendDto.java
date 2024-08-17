package com.won.myongjiCamp.dto.Fcm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FcmSendDto { //client에서 전달받은 객체
    private ArrayList<String> to;
    private String title;
    private String body;
}
