package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.Fcm.FcmSendDto;
import com.won.myongjiCamp.dto.NotificationResponseDto;
import com.won.myongjiCamp.dto.PageDto;
import com.won.myongjiCamp.dto.ResponseDto;
import com.won.myongjiCamp.dto.TokenDto;
import com.won.myongjiCamp.model.Notification;
import com.won.myongjiCamp.service.FcmService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class FcmApiController {
    private final FcmService fcmService;


    @PostMapping("/api/v1/fcm/send")
    public ResponseEntity pushMessage(@RequestBody @Validated FcmSendDto fcmSendDto) throws IOException {
        System.out.println("푸시 메세지 전송");
        int result = fcmService.sendMessageTo(fcmSendDto);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @PostMapping("/token/post")
    public void postToken(@RequestBody @Validated TokenDto tokenDto){
//        System.out.println("token : " + tokenDto.getToken());
    }

    //Redis에 expoToken 저장
    @PostMapping("/send/expoToken")
    public ResponseDto<String> saveExpoToken(@AuthenticationPrincipal PrincipalDetail principalDetail, @RequestBody @Validated TokenDto tokenDto){
        fcmService.saveExpoToken(principalDetail.getMember(),tokenDto);
        return new ResponseDto<String>(HttpStatus.OK.value(),"expoToken save");
    }
    //Redis에 expoToken 삭제
    @PostMapping("/delete/expoToken")
    public ResponseDto<String> deleteExpoToken(@AuthenticationPrincipal PrincipalDetail principalDetail, @RequestBody @Validated TokenDto tokenDto){
        fcmService.deleteExpoToken(principalDetail.getMember(),tokenDto);
        return new ResponseDto<String> (HttpStatus.OK.value(),"expoToken delete");
    }

    //알림 목록
    @GetMapping("/get/notifications")
    public Result getNotifications(@AuthenticationPrincipal PrincipalDetail principalDetail,@ModelAttribute @Valid PageDto pageDto){
        Page<Notification> notificationPage = fcmService.findAllNotifications(principalDetail.getMember(),pageDto.getPageNum());
        List<NotificationResponseDto> notificationList = notificationPage.stream()
                .map(NotificationResponseDto::new)
                .collect(Collectors.toList());
        return new Result(notificationList);
    }
    //알림 읽음
    @PostMapping("/read/notification/{notificationId}")
    public ResponseDto<String> isRead(@AuthenticationPrincipal PrincipalDetail principalDetail,@PathVariable long notificationId){
        System.out.println(principalDetail.getMember());
        fcmService.isRead(principalDetail.getMember(),notificationId);
        return new ResponseDto<String> (HttpStatus.OK.value(),"isread is true");
    }



    //한 달이 지난 알림은 삭제 && page로 보여주기

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }



}
