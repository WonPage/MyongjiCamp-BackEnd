package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.security.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.Fcm.FcmSendDto;
import com.won.myongjiCamp.dto.response.NotificationResponse;
import com.won.myongjiCamp.dto.PageDto;
import com.won.myongjiCamp.dto.response.ResponseDto;
import com.won.myongjiCamp.dto.TokenDto;
import com.won.myongjiCamp.model.Notification;
import com.won.myongjiCamp.service.FcmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class FcmApiController {
    private final FcmService fcmService;

    @PostMapping("/api/v1/fcm/send")
    public ResponseDto<String> pushMessage(@RequestBody @Validated FcmSendDto fcmSendDto) throws IOException {
        int result = fcmService.sendMessageTo(fcmSendDto);
        return new ResponseDto<String>(HttpStatus.OK.value(), "댓글 작성");
    }

    @PostMapping("/fcmToken")
    public ResponseDto<String> fcmToken(@AuthenticationPrincipal PrincipalDetail principalDetail, @RequestBody @Validated TokenDto tokenDto){
        System.out.println("hi");
        fcmService.fcmToken(principalDetail.getMember(),tokenDto);
        System.out.println("fcm : "+tokenDto.getToken());
        return new ResponseDto<String>(HttpStatus.OK.value(),"fcm save");
    }

    //Redis에 expoToken 삭제 -> fcmToken 삭제
    @PostMapping("/delete/expoToken")
    public ResponseDto<String> deleteExpoToken(@AuthenticationPrincipal PrincipalDetail principalDetail, @RequestBody @Validated TokenDto tokenDto){
        fcmService.deleteExpoToken(principalDetail.getMember(),tokenDto);
        return new ResponseDto<String> (HttpStatus.OK.value(),"expoToken delete");
    }

    //알림 목록
    @GetMapping("/get/notifications")
    public ResponseDto<List<NotificationResponse>> getNotifications(@AuthenticationPrincipal PrincipalDetail principalDetail,@ModelAttribute @Valid PageDto pageDto){
        Page<Notification> notificationPage = fcmService.findAllNotifications(principalDetail.getMember(),pageDto.getPageNum());

        List<NotificationResponse> notificationList = notificationPage.stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());

        for(int i=0; i<notificationList.size(); i++){
            System.out.println(notificationList.get(i).getNotificationStatus());
        }
        return new ResponseDto<> (HttpStatus.OK.value(),notificationList);

    }
    //알림 읽음
    @PostMapping("/read/notification/{notificationId}")
    public ResponseDto<String> isRead(@AuthenticationPrincipal PrincipalDetail principalDetail,@PathVariable long notificationId){
        System.out.println(principalDetail.getMember());
        fcmService.isRead(principalDetail.getMember(),notificationId);
        return new ResponseDto<String> (HttpStatus.OK.value(),"isread is true");
    }
}
