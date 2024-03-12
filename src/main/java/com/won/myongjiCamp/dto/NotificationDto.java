package com.won.myongjiCamp.dto;

import com.won.myongjiCamp.model.NotificationType;
import lombok.Data;

@Data
public class NotificationDto {

    private Long id;

    private String content;

    private boolean isRead;

    private NotificationType notificationType;

    private Long receiverId;

    private Long targetCommentId;

    private Long targetBoardId;

//    private String url;

}
