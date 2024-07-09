package com.won.myongjiCamp.dto.Fcm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FcmMessageDto {
    private boolean validateOnly;
    private FcmMessageDto.Message message;
    @Builder
    public static class Notification{
        private String title;
        private String body;
        private String image;
    }
    @Builder
    public static class Message{
        private FcmMessageDto.Notification notification;
        private String token;
    }
}
