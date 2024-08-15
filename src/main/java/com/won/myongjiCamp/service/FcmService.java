package com.won.myongjiCamp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.won.myongjiCamp.dto.CommentDto;
import com.won.myongjiCamp.dto.Fcm.FcmMessageDto;
import com.won.myongjiCamp.dto.Fcm.FcmSendDto;
import com.won.myongjiCamp.dto.TokenDto;
import com.won.myongjiCamp.model.*;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.repository.BoardRepository;
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

    public int sendMessageTo(FcmSendDto fcmSendDto) throws IOException{
        String message = makeMessage(fcmSendDto);
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters() // 한글 깨짐 해결
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+getAccessToken());

        HttpEntity entity = new HttpEntity<>(message, headers);

        String API_URL = "<https://fcm.googleapis.com/v1/projects/mjcamp-67915/messages:send>";
        ResponseEntity response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
        System.out.println("response "+response.getStatusCode());
        return response.getStatusCode() == HttpStatus.OK ? 1:0;
    }

    private String getAccessToken() throws IOException{
        String firebaseConfigPath = "firebase/mjcamp-67915-firebase-adminsdk-ydkil-e1224a7415.json";
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("<https://www.googleapis.com/auth/cloud-platform>"));
        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }


    // fcm 전송 정보를 기반으로 메시지 구성(Object -> String)
    public String makeMessage(FcmSendDto fcmSendDto) throws JsonProcessingException{
        ObjectMapper om = new ObjectMapper();
        FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
                .message(FcmMessageDto.Message.builder()
                        .token(fcmSendDto.getToken())
                        .notification(FcmMessageDto.Notification.builder()
                                        .title(fcmSendDto.getTitle())
                                        .body(fcmSendDto.getBody())
                                        .image(null)
                                        .build()
                        ).build())
                .validateOnly(false).build();
        return om.writeValueAsString(fcmMessageDto);
    }
    @Transactional
    public void saveExpoToken(Member mem, TokenDto tokenDto){
        Member member = memberRepository.findById(mem.getId())
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 회원입니다."));
        // expoToken이 그 사람의 expoToken이 아닐 수 도 있음
        redisTemplate.opsForList().rightPush("expo notification token:" + member.getEmail(), tokenDto.getToken());

    }
    public Notification createNotification(Member member, Board board, String content){
        return Notification.builder()
                .targetBoard(board)
                .content(content) // 댓글 내용
                .isRead(false)
                .receiver(member)
                .build();
    }

    @Transactional
    public void sendNotification(Member mem, CommentDto commentDto,Long id){
        Member member = memberRepository.findById(mem.getId()) // 댓글 쓴 사람
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 회원입니다."));

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        List<String> boardWriterTokens = redisTemplate.opsForList().range("expo notification token:" + board.getMember().getEmail(), 0, -1);
        List<String> commentWriterTokens = redisTemplate.opsForList().range("expo notification token:" + member.getEmail(), 0, -1); // 댓글 작성자(대댓용)

        String reqURL = "https://exp.host/--/api/v2/push/send";
        try{
            URL url = new URL(reqURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            ArrayList<String> tos = new ArrayList<>(); //보낼 사람들
            NotificationMessage message = new NotificationMessage();


            ArrayList<Notification> notifications = new ArrayList<>();
            if(commentDto.getCdepth() == 0 ){// 댓글
                if(boardWriterTokens != null && !boardWriterTokens.isEmpty()) {
                    //게시글 작성자 한 사람이 여러개의 기기에서 로그인 했을 경우 모든 기기에게 알림을 보내야 한다.(tos에 추가)
                    tos.addAll(boardWriterTokens);
                    message = NotificationMessage.builder()
                            .to(tos)
                            .sound("default")
                            .title("명지캠프")
                            .body("댓글이 달렸습니다 : "+commentDto.getContent())
                            .data(new HashMap<>())
                            .build();
                    //모든 기기에 알림을 보냈지만 쌓이는 알림은 하나여야 한다.
                    notifications.add(createNotification(board.getMember(),board, message.getBody()));
                }
            }
            else{ // 대댓글
                if(boardWriterTokens != null && !boardWriterTokens.isEmpty()) {
                    tos.addAll(boardWriterTokens);
                    notifications.add(createNotification(board.getMember(),board, "대댓글이 달렸습니다 : "+commentDto.getContent()));
                }
                if(commentWriterTokens != null && !commentWriterTokens.isEmpty()){
                    tos.addAll(commentWriterTokens);
                    notifications.add(createNotification(member,board, "대댓글이 달렸습니다 : "+commentDto.getContent()));

                }
                if(!tos.isEmpty()) {
                    message = NotificationMessage.builder()
                            .to(tos)
                            .sound("default")
                            .title("명지캠프")
                            .body("대댓글이 달렸습니다 : "+commentDto.getContent())
                            .data(new HashMap<>())
                            .build();
                }
            }
            for(int i=0; i<notifications.size(); i++){
                notificationRepository.save(notifications.get(i));
            }
            //HttpURLConnection 설정 값 셋팅(필수 헤더 세팅)
            con.setRequestMethod("POST"); //인증 토큰 전송
            con.setRequestProperty("Accept","application/json");
            con.setRequestProperty("Accept-encoding","gzip, deflate");
            con.setRequestProperty("Content-type","application/json"); //인증 토큰 전송
            con.setDoOutput(true); //OutputStream으로 POST     데이터를 넘겨주겠다는 옵션

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonInputString = objectMapper.writeValueAsString(message);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Notification sent successfully.");
            } else {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream()))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("Response Error: " + response.toString());
                }
                System.out.println("Failed to send notification. Response Code: " + responseCode);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void deleteExpoToken(Member member,TokenDto tokenDto){
        redisTemplate.opsForList().remove("expo notification token:" + member.getEmail(),0,tokenDto.getToken());
    }

    public Page<Notification> findAllNotifications(Member mem, int pageNum){
        Member member = memberRepository.findById(mem.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Pageable pageable = PageRequest.of(pageNum, 30, Sort.by("createDate").descending());
        Page<Notification> notifications = notificationRepository.findAllByReceiver(member, pageable);

        return notifications;
    }

    @Transactional
    public void isRead(Member mem,long notificationId){
        Member member = memberRepository.findById(mem.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));
        notification.setRead(true);
    }



}

