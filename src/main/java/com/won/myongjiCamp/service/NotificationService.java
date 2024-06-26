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
import io.lettuce.core.ScriptOutputType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;
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
    @Transactional
    public SseEmitter subscribe(Long clientid,String lastEventId){ //SseEmitter 구독(서버 , 클라이언트 연결)

        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT); // 클라이언트를 위한 sseEmitter
        String emitterId = clientid+"_"+System.currentTimeMillis(); //userName대신 clientId로 해도 되나?
        sseRepository.save(emitterId, sseEmitter); // 사용자 id 기반 이벤트 Emitter 생성
        System.out.println("sseEmitter "+sseEmitter);

        sseEmitter.onCompletion(()->sseRepository.deleteById(emitterId)); //완료 시, 타임아웃 시, 에러 발생 시
        sseEmitter.onTimeout(() -> sseRepository.deleteById(emitterId));
        sseEmitter.onError((e) -> sseRepository.deleteById(emitterId));

        //503 에러 방지 더미 이벤트 전송
        sendToClient(sseEmitter, emitterId,"EventStream Created. [userId="+clientid+"]\n" );
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
                    .name("open")
                    .data(data, MediaType.APPLICATION_JSON));
//            System.out.println("여기는 왔엉:" + sseRepository.findAllEmitterStartsWithMemberId(String.valueOf(clientid)));
            System.out.println("client id" + emitterId + data);
        }catch (IOException exception){
            System.out.println("에러");
            sseRepository.deleteById(emitterId);
            sseEmitter.completeWithError(exception);
        }

    }
    // 댓글 알림 전송 - 게시글 작성자에게
    @Transactional
    public void sendComment(Board board, CommentDto commentDto){
        System.out.println("hi");
        Notification notification = createCommentNotification(board,commentDto);

        String id = String.valueOf(board.getMember().getId());

        // 로그인 한 유저의 SseEmitter 모두 가져오기
        Map<String, SseEmitter> sseEmitterMap = sseRepository.findAllEmitterStartsWithMemberId(id);

        sseEmitterMap.forEach(
                (key, emitter)->{
//                System.out.println(key+"key"+emitter+"emitter");
                    sseRepository.saveEventCache(key, notification); //저장
                    sendToClient(emitter, key, createNotificationResponseDto(notification.getId(),notification.getContent(),notification.isRead(),notification.getNotificationType(),notification.getReceiver().getId(), notification.getTargetComment().getId(), notification.getTargetBoard().getId())); // 전송
                }
        );

    }

    // 대댓글 알림 전송 - 게시글 작성자, 부모 댓글 작성자에게
    @Transactional
    public void sendChildComment(Board board, CommentDto commentDto){
        Notification notification = createCommentNotification(board,commentDto);

        String boardId = String.valueOf(board.getMember().getId()); // 게시글 작성자
        String parentId = String.valueOf(commentDto.getParentId()); // 부모 댓글 작성자

        // 로그인 한 유저의 SseEmitter 모두 가져오기
        Map<String, SseEmitter> boardSseEmitterMap = sseRepository.findAllEmitterStartsWithMemberId(boardId);
        Map<String, SseEmitter> parentSseEmitterMap = sseRepository.findAllEmitterStartsWithMemberId(parentId);

        // 대댓글용, notificationResponseDto에 부모 댓글 id 추가
        boardSseEmitterMap.forEach(
                (key, emitter)->{
//                System.out.println(key+"key"+emitter+"emitter");
                    sseRepository.saveEventCache(key, notification);
                    sendToClient(emitter, key, createNotificationResponseDto(notification.getId(),notification.getContent(),notification.isRead(),notification.getNotificationType(),notification.getReceiver().getId(), notification.getTargetComment().getId(), notification.getTargetBoard().getId(),notification.getTargetComment().getParent().getId())); // 전송
                }
        );
        parentSseEmitterMap.forEach(
                (key, emitter)->{
//                System.out.println(key+"key"+emitter+"emitter");
                    sseRepository.saveEventCache(key, notification);
                    sendToClient(emitter, key, createNotificationResponseDto(notification.getId(),notification.getContent(),notification.isRead(),notification.getNotificationType(),notification.getReceiver().getId(), notification.getTargetComment().getId(), notification.getTargetBoard().getId(),notification.getTargetComment().getParent().getId())); // 전송
                }
        );

    }

    // 댓글, 대댓글 notification 생성
    @Transactional
    private Notification createCommentNotification(Board board,CommentDto commentDto){

        Comment comment = commentRepository.findById(commentDto.getId())
                    .orElseThrow(()->new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        if(comment.getCdepth() == 0){
            Notification notification = Notification.builder()
                    .content(comment.getContent())
                    .isRead(false)
                    .notificationType(NotificationType.COMMENT)
                    .receiver(board.getMember())
                    .targetBoard(board)
                    .targetComment(comment)
//                    .url(notificationDto.getUrl())
                    .build();
            notificationRepository.save(notification);
            return notification;
        }
        else{ //getCdept == 1
            Notification notification = Notification.builder()
                    .content(comment.getContent())
                    .isRead(false)
                    .notificationType(NotificationType.CHILDCOMMENT)
                    .receiver(board.getMember())
                    .targetBoard(board)
                    .targetComment(comment)
//                    .url(notificationDto.getUrl())
                    .build();
            notificationRepository.save(notification);
            return notification;
        }
    }

/*    @Transactional
    private Notification createApplicationNotification(){
        return
    }*/

    // 게시글 삭제
    @Transactional
    public void delete(Long id){
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("해당 알림이 존재하지 않습니다."));
        notificationRepository.delete(notification);
    }




}
