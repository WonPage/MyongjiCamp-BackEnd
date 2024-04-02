package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.ResponseDto;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.repository.MemberRepository;
import com.won.myongjiCamp.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
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
    private final MemberRepository memberRepository;

    @GetMapping(value = "/api/auth/noification/subscribe",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(@AuthenticationPrincipal PrincipalDetail principalDetail, @RequestHeader(value = "Last-Event-ID", required = false,defaultValue = "")String lastEventId){
//    public SseEmitter subscribe(@AuthenticationPrincipal PrincipalDetail principalDetail, @RequestParam(value = "lastEventId", required = false,defaultValue = "")String lastEventId){
        System.out.println(principalDetail.getUsername()+"이"+lastEventId);
        return notificationService.subscribe(principalDetail.getMember().getId(), lastEventId);
    }


    // 게시글 삭제, id는 게시글 id
    @DeleteMapping("/api/auth/noification/{id}")
    public ResponseDto<String> deleteNotification(@PathVariable long id){
        notificationService.delete(id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "알림 삭제되었습니다.");
    }


/*
    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
*/




}
