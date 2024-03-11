package com.won.myongjiCamp.service;

import com.won.myongjiCamp.controller.api.NotificationApiController;
import com.won.myongjiCamp.dto.CommentDto;
import com.won.myongjiCamp.dto.NotificationDto;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.Notification;
import com.won.myongjiCamp.model.NotificationType;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.Comment;
import com.won.myongjiCamp.model.board.RecruitBoard;
import com.won.myongjiCamp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

import static com.won.myongjiCamp.dto.NotificationResponseDto.createNotificationResponseDto;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final NotificationRepository notificationRepository;
//    private final EmitterRepository emitterRepository;
    private final SseRepository sseRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;


    //SseEmitter : 서버 -> 클라이언트, 데이터 실시간 전송
    // 클라이언트, 서버간 연결 유지
    public SseEmitter subscribe(Long clientid,String lastEventId){ //SseEmitter 구독(서버 , 클라이언트 연결)

        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT); // 클라이언트를 위한 sseEmitter
        String emitterId = clientid+"_"+System.currentTimeMillis(); //userName대신 clientId로 해도 되나?
        sseRepository.save(emitterId, sseEmitter); // 사용자 id 기반 이벤트 Emitter 생성

        sseEmitter.onCompletion(()->sseRepository.deleteById(emitterId)); //완료 시, 타임아웃 시, 에러 발생 시
        sseEmitter.onTimeout(() -> sseRepository.deleteById(emitterId));
        sseEmitter.onError((e) -> sseRepository.deleteById(emitterId));

        //503 에러 방지 더미 이벤트 전송?
        sendToClient(sseEmitter, emitterId,"EventStream Created. [userId="+clientid+"]" );
        //클라이언트가 미수신한 event 목록이 존재할 경우 전송해 event 유실 예방
        if(!lastEventId.isEmpty()){
            Map<String, Object> eventCaches = sseRepository.findAllEventCacheStartWithByMemberId(String.valueOf(clientid));
            eventCaches.entrySet().stream()
                    .filter(entry->lastEventId.compareTo(entry.getKey())<0)
                    .forEach(entry->sendToClient(sseEmitter,entry.getKey(), entry.getValue()));
        }
        return sseEmitter;
    }


    //event 전송
    private void sendToClient(SseEmitter sseEmitter,String emitterId, Object data){
        try{
            sseEmitter.send(SseEmitter.event()
                    .id(emitterId)
                    .name("sse")
                    .data(data, MediaType.APPLICATION_JSON));
        }catch (IOException exception){
            sseRepository.deleteById(emitterId);
            sseEmitter.completeWithError(exception);
        }

    }
/*public void send(Member receiver, NotificationDto notificationDto){
        Notification notification = createNotification(notificationDto, receiver);
        String id = String.valueOf(receiver.getId());

        // 로그인 한 유저의 SseEmitter 모두 가져오기
    Map<String, SseEmitter> sseEmitterMap = sseRepository.findAllEmitterStartsWithMemberId(id);
    sseEmitterMap.forEach(
            (key, emitter)->{
                sseRepository.saveEventCache(key, notification); //저장
                sendToClient(emitter, key,notification); // 전송
            }
    );

}*/
public void sendComment(Member receiver, CommentDto commentDto){
        Notification notification = createCommentNotification(commentDto);
        String id = String.valueOf(receiver.getId());

        // 로그인 한 유저의 SseEmitter 모두 가져오기
    Map<String, SseEmitter> sseEmitterMap = sseRepository.findAllEmitterStartsWithMemberId(id);
    sseEmitterMap.forEach(
            (key, emitter)->{
                sseRepository.saveEventCache(key, notification); //저장
                sendToClient(emitter, key, createNotificationResponseDto(notification.getId(),notification.getContent(),notification.isRead(),notification.getNotificationType(),notification.getReceiver().getId(), notification.getTargetComment().getId(), notification.getTargetBoard().getId())); // 전송
            }
    );

}


/*    public void notifyComment(Long postId){
        Board board = boardRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
//        Comment comment = commentRepository.findByBoard(board.)

        if(NotificationApiController.sseEmitters.containsKey()) {
            SseEmitter sseEmitter = NotificationApiController.sseEmitters.get();
        }

    }*/

    private Notification createCommentNotification(CommentDto commentDto){ // 댓글 알림 - 게시글 작성자에게
            Board board = boardRepository.findById(commentDto.getBoardId())
                    .orElseThrow(()->new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
            Comment comment = commentRepository.findById(commentDto.getId())
                    .orElseThrow(()->new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

            if(comment.getCdepth() == 0){
                return Notification.builder()
                        .content(comment.getContent())
                        .isRead(false)
                        .notificationType(NotificationType.COMMENT)
                        .receiver(board.getMember())
                        .targetComment(comment)
                        .targetBoard(board)
//                    .url(notificationDto.getUrl())
                        .build();
            }
            else if(comment.getCdepth() == 1){
                return Notification.builder()
                        .content(comment.getContent())
                        .isRead(false)
                        .notificationType(NotificationType.CHILDCOMMENT)
                        .receiver(board.getMember())
                        .targetComment(comment)
                        .targetBoard(board)
//                    .url(notificationDto.getUrl())
                        .build();
            }
            else{
                return null;
            }


//            notificationRepository.save(notification);
        }

/*    private Notification createCommentNotification(CommentDto commentDto, Member member){ // 댓글 알림
        if(notificationDto.getNotificationType().equals(NotificationType.COMMENT)) { //댓글 - 게시글 작성자에게
            Board board = boardRepository.findById(commentDto.getBoardId())
                    .orElseThrow(()->new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
            Comment comment = commentRepository.findById(commentDto.getId())
                    .orElseThrow(()->new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

            return Notification.builder()
                    .receiver(member)
                    .notificationType(NotificationType.COMMENT)
                    .targetComment(comment)
                    .content(comment.getContent())
                    .isRead(false)
                    .url(notificationDto.getUrl())
                    .build();

//            notificationRepository.save(notification);
        }
        else if(notificationDto.getNotificationType().equals(NotificationType.CHILDCOMMENT)){ //대댓글
            Comment comment = commentRepository.findById(notificationDto.getTargetCommentId())
                    .orElseThrow(()->new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
            return Notification.builder()
                    .receiver(member)
                    .notificationType(NotificationType.CHILDCOMMENT)
                    .targetComment(comment)
                    .content(comment.getContent())
                    .isRead(false)
                    .url(notificationDto.getUrl())
                    .build();

//            notificationRepository.save(notification);
        }
*//*        else if(notificationDto.getNotificationType().equals(NotificationType.POST)){ //게시글
            return Notification.builder()
                    .receiver(member)
                    .notificationType(NotificationType.POST)
                    .isRead(false)
                    .build(); //여기 완전 수정해야됨
        }*//*
        else{
            return null;
        }
    }*/







    // 게시글 작성자에게, 댓글 알림
/*    private void notifyComment(Long postId) {
        Board board = boardRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

//        Comment receiveComment = commentRepository.findByBoard(board)

        Long postWriterId = board.getMember().getId();

    }*/


}
