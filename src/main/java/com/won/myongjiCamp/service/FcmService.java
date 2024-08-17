package com.won.myongjiCamp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.won.myongjiCamp.dto.CommentDto;
import com.won.myongjiCamp.dto.Fcm.FcmMessageDto;
import com.won.myongjiCamp.dto.Fcm.FcmSendDto;
import com.won.myongjiCamp.dto.RoleAssignmentDto;
import com.won.myongjiCamp.dto.TokenDto;
import com.won.myongjiCamp.model.*;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.Comment;
import com.won.myongjiCamp.repository.BoardRepository;
import com.won.myongjiCamp.repository.CommentRepository;
import com.won.myongjiCamp.repository.MemberRepository;
import com.won.myongjiCamp.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class FcmService { //Fcm과 통신해 client에서 받은 정보를 기반으로 메시지 전송
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public int sendMessageTo(FcmSendDto fcmSendDto) throws IOException {
        List<String> messages = makeMessage(fcmSendDto);
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters() // 한글 깨짐 해결
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());

        List<Integer> results = new ArrayList<>();
        for (String message : messages) {
            System.out.println(message);
            HttpEntity entity = new HttpEntity<>(message, headers);
            String API_URL = "<https://fcm.googleapis.com/v1/projects/mjcamp-67915/messages:send>";
//            String API_URL = "https://fcm.googleapis.com/v1/projects/fcmtest-94004/messages:send";

            ResponseEntity response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
            System.out.println("response " + response.getStatusCode());

            results.add(response.getStatusCode() == HttpStatus.OK ? 1 : 0);
        }
        if (results.contains(0)) {
            return 0;
        } else {
            return 1;
        }
    }

    public String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/mjcamp-67915-firebase-adminsdk-ydkil-e1224a7415.json";
//        String firebaseConfigPath = "firebase/fcmtest-94004-firebase-adminsdk-bxn5z-ea38c5420d.json";
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }


    // fcm 전송 정보를 기반으로 메시지 구성(Object -> String)
    public List<String> makeMessage(FcmSendDto fcmSendDto) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        List<String> messageList = new ArrayList<>();
        for (String fcmToken : fcmSendDto.getTo()) {
            FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
                    .message(FcmMessageDto.Message.builder()
                            .token(fcmToken)
                            .notification(FcmMessageDto.Notification.builder()
                                    .title(fcmSendDto.getTitle())
                                    .body(fcmSendDto.getBody())
                                    .image(null)
                                    .build()
                            ).build())
                    .validateOnly(false).build();
            messageList.add(om.writeValueAsString(fcmMessageDto));
        }
        return messageList;
    }

    public Notification createNotification(Member member, Board board, String content) {
        return Notification.builder()
                .targetBoard(board)
                .content(content) // 댓글 내용
                .isRead(false)
                .receiver(member)
                .build();
    }

    final private CommentRepository commentRepository;
    @Transactional
    public void sendNotification(Member mem, CommentDto commentDto, Long id) throws IOException {
        Member member = memberRepository.findById(mem.getId()) // 댓글 작성자
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        List<String> boardWriterTokens = redisTemplate.opsForList().range("expo notification token:" + board.getMember().getEmail(), 0, -1);


        ArrayList<String> tos = new ArrayList<>(); //보낼 사람들
        FcmSendDto fcmSendMessage = new FcmSendDto(); //fcm으로 보낼 알림

        ArrayList<Notification> notifications = new ArrayList<>(); // 알림 목록을 위해 sql에 저장시킬 알림들
        if (commentDto.getCdepth() == 0) {// 댓글
            if(board.getMember().getId().equals(mem.getId())){
                return;
            }
            if (boardWriterTokens != null && !boardWriterTokens.isEmpty()) {
                //게시글 작성자 한 사람이 여러개의 기기에서 로그인 했을 경우 모든 기기에게 알림을 보내야 한다.(tos에 추가)
                tos.addAll(boardWriterTokens);
                fcmSendMessage = FcmSendDto.builder()
                        .to(tos)
                        .title("명지캠프")
                        .body("댓글이 달렸습니다 : " + commentDto.getContent())
                        .build();
                //모든 기기에 알림을 보냈지만 쌓이는 알림은 하나여야 한다.
                notifications.add(createNotification(board.getMember(), board, fcmSendMessage.getBody()));
            }
        } else { // 대댓글
            Comment comment = commentRepository.findById(commentDto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
            Member parentMember = memberRepository.findById(comment.getWriter().getId()) //대댓글의 부모 댓글 작성자
                    .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 회원입니다."));
            List<String> commentWriterTokens = redisTemplate.opsForList().range("expo notification token:" + parentMember.getEmail(), 0, -1); // 댓글 작성자(대댓용)

            if(board.getMember().getId().equals(mem.getId())){
                return;
            }
            if(parentMember.getId().equals(mem.getId())){
                return;
            }
            if (boardWriterTokens != null && !boardWriterTokens.isEmpty()) {
                tos.addAll(boardWriterTokens);
                notifications.add(createNotification(board.getMember(), board, "대댓글이 달렸습니다 : " + commentDto.getContent()));
            }
            if (commentWriterTokens != null && !commentWriterTokens.isEmpty()) {
                tos.addAll(commentWriterTokens);
                notifications.add(createNotification(parentMember, board, "대댓글이 달렸습니다 : " + commentDto.getContent()));
            }
            if (!tos.isEmpty()) {
                fcmSendMessage = FcmSendDto.builder()
                        .to(tos)
                        .title("명지캠프")
                        .body("대댓글이 달렸습니다 : " + commentDto.getContent())
                        .build();
            }
        }

        for (int i = 0; i < notifications.size(); i++) {
            notificationRepository.save(notifications.get(i));
        }
        if (fcmSendMessage != null) {
            sendMessageTo(fcmSendMessage);
        } else {
            System.out.println("fcm nothing");
        }
    }

    public void deleteExpoToken(Member member, TokenDto tokenDto) {
        redisTemplate.opsForList().remove("expo notification token:" + member.getEmail(), 0, tokenDto.getToken());
    }

    public Page<Notification> findAllNotifications(Member mem, int pageNum) {
        Member member = memberRepository.findById(mem.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Pageable pageable = PageRequest.of(pageNum, 30, Sort.by("createDate").descending());
        Page<Notification> notifications = notificationRepository.findAllByReceiver(member, pageable);

        return notifications;
    }

    @Transactional
    public void isRead(Member mem, long notificationId) {
        Member member = memberRepository.findById(mem.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));
        notification.setRead(true);
    }

    @Transactional
    public void fcmToken(Member mem, TokenDto tokenDto) {
        Member member = memberRepository.findById(mem.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        //이미 redis에 존재하면 저장 x
        redisTemplate.opsForList().rightPush("expo notification token:" + member.getEmail(), tokenDto.getToken());

        //여기여기 -> redis에 중복되는 토큰이 저장되는 상황이 있는지 확인하기 : 있다면 아래 코드 사용
/*
        List<String> existToken = redisTemplate.opsForList().range("expo notification token:" + member.getEmail(), 0, -1);

        if(!existToken.contains(tokenDto.getToken())){
        }
        */
    }


}

