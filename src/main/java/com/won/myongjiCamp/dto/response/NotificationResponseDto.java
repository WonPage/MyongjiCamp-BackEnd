package com.won.myongjiCamp.dto.response;

import com.won.myongjiCamp.model.Notification;
import com.won.myongjiCamp.model.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@AllArgsConstructor
@Builder
@Data
public class NotificationResponseDto {
    private Long id;

    private String content;

    private boolean isRead;

    private Long receiverId;

    private Long targetBoardId;

    private Timestamp createDate;

    private NotificationStatus notificationStatus;


    public NotificationResponseDto(Notification notification){
        this.id = notification.getId();
        this.content = notification.getContent();
        this.isRead = notification.isRead();
        this.receiverId = notification.getReceiver().getId();
        this.targetBoardId = notification.getTargetBoard().getId();
        this.targetBoardId = notification.getTargetBoard().getId();
        this.createDate = notification.getCreateDate();
        this.notificationStatus = notification.getNotificationStatus();
    }

}
