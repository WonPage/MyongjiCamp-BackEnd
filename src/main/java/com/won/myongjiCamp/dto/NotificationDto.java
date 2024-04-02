package com.won.myongjiCamp.dto;

import com.won.myongjiCamp.model.NotificationType;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class NotificationDto {

    private Long id;

    private String content;

    private boolean isRead;

    private NotificationType notificationType;

    private Long receiverId;

    private Long targetCommentId;

    private Long targetParentCommentId; // 대댓글의 경우 모댓글을 알아야 해서 필요

    private Long targetBoardId;

//    private String url;

}
