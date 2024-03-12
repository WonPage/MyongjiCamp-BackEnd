package com.won.myongjiCamp.dto;

import com.won.myongjiCamp.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class NotificationResponseDto {
    private Long id;

    private String content;

    private boolean isRead;

    private NotificationType notificationType;

    private Long receiverId;

    private Long targetCommentId;

    private Long targetBoardId;



    public static NotificationResponseDto createNotificationResponseDto(Long id, String content, boolean isRead, NotificationType notificationType,Long receiverId, Long targetCommentId,Long targetBoardId){
        return NotificationResponseDto.builder()
                .id(id)
                .content(content)
                .isRead(isRead)
                .notificationType(notificationType)
                .receiverId(receiverId)
                .targetCommentId(targetCommentId)
                .targetBoardId(targetBoardId)
                .build();
    }

}
