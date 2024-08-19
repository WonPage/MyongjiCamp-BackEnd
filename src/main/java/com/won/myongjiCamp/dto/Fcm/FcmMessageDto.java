package com.won.myongjiCamp.dto.Fcm;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class FcmMessageDto {
    private boolean validateOnly;
    private FcmMessageDto.Message message;
    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification{
        private String title;
        private String body;
        private String image;
    }
    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message{
        private FcmMessageDto.Notification notification;
        private String token;
    }
}
