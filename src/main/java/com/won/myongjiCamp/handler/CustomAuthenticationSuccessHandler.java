package com.won.myongjiCamp.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.won.myongjiCamp.config.jwt.JwtTokenUtil;
import com.won.myongjiCamp.dto.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private JwtTokenUtil jwtTokenUtil;

    public CustomAuthenticationSuccessHandler(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // JWT 토큰을 생성
        String token = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> data = new HashMap<>();
        data.put("message", "로그인 성공");
        data.put("token", token);

        response.getWriter().println(new ObjectMapper().writeValueAsString(
                new ResponseDto<>(response.getStatus(), data)));
    }
}

