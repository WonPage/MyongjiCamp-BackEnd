package com.won.myongjiCamp.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.won.myongjiCamp.config.jwt.JwtTokenUtil;
import com.won.myongjiCamp.dto.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.rmi.server.LogStream.log;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        //access 토큰 생성
        String token = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());
        //refresh 토큰 생성
        String refreshToken = jwtTokenUtil.generateRefreshToken((UserDetails) authentication.getPrincipal());

        // Redis에 Refresh Token 저장
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        redisTemplate.opsForValue().set("refresh token:" + email, refreshToken);
        redisTemplate.expire("refresh token:" + email, jwtTokenUtil.getRefreshExpirationTime(), TimeUnit.MILLISECONDS);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> data = new HashMap<>();
        data.put("message", "로그인 성공");
        data.put("token", token);
        data.put("refreshToken", refreshToken);

        response.getWriter().println(new ObjectMapper().writeValueAsString(
                new ResponseDto<>(response.getStatus(), data)));
    }
}

