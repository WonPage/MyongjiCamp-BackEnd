package com.won.myongjiCamp.dto;

import com.won.myongjiCamp.model.Notification;
//import com.won.myongjiCamp.model.NotificationType;
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

//    private NotificationType notificationType;

    private Long receiverId;

//    private Long targetCommentId;

    private Long targetBoardId;

    private Timestamp createDate;

//    private Long targetParentCommentId;

    public NotificationResponseDto(Notification notification){
        this.id = notification.getId();
        this.content = notification.getContent();
        this.isRead = notification.isRead();
        this.receiverId = notification.getReceiver().getId();
        this.targetBoardId = notification.getTargetBoard().getId();
        this.createDate = notification.getCreateDate();
    }

    //댓글용
/*    public static NotificationResponseDto createNotificationResponseDto(Long id, String content, boolean isRead, NotificationType notificationType,Long receiverId, Long targetCommentId,Long targetBoardId){
        return NotificationResponseDto.builder()
                .id(id)
                .content(content)
                .isRead(isRead)
//                .notificationType(notificationType)
                .receiverId(receiverId)
//                .targetCommentId(targetCommentId)
                .targetBoardId(targetBoardId)
                .build();
    }

    //대댓글용 (parentCommentId 추가)
    public static NotificationResponseDto createNotificationResponseDto(Long id, String content, boolean isRead, NotificationType notificationType,Long receiverId, Long targetCommentId,Long targetBoardId, Long targetParentCommentId){
        return NotificationResponseDto.builder()
                .id(id)
                .content(content)
                .isRead(isRead)
//                .notificationType(notificationType)
                .receiverId(receiverId)
//                .targetCommentId(targetCommentId)
                .targetBoardId(targetBoardId)
//                .targetParentCommentId(targetParentCommentId)
                .build();
    }*/

}
