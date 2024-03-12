package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.ResponseDto;
import com.won.myongjiCamp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
public class NotificationApiController {
    public static Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();
    private final NotificationService notificationService;
    @GetMapping(value = "/api/auth/noification/subscribe",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseDto<String> subscribe(@AuthenticationPrincipal PrincipalDetail principalDetail, @RequestHeader(value = "Last-Event-ID", required = false,defaultValue = "")String lastEventId){
        SseEmitter sseEmitter = notificationService.subscribe(principalDetail.getMember().getId(), lastEventId);

        return new ResponseDto(HttpStatus.OK.value(), "구독이 완료되었습니다.");
    }



}
