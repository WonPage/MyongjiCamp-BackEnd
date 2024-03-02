package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.jwt.JwtTokenUtil;
import com.won.myongjiCamp.dto.request.CreateMemberDto;
import com.won.myongjiCamp.dto.request.EmailDto;
import com.won.myongjiCamp.dto.ResponseDto;
import com.won.myongjiCamp.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final StringRedisTemplate redisTemplate;

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    @PostMapping("/api/members")
    public ResponseDto saveMember(@RequestBody @Valid CreateMemberDto request) {
        Long id = memberService.join(request.getEmail(),request.getPassword(), request.getNickname(),request.getProfileIcon());
        Map<String, Long> data = new HashMap<>();
        data.put("id", id);
        return new ResponseDto(HttpStatus.OK.value(), data);
    }

    //이메일 전송
    @PostMapping("/api/email")
    public ResponseDto sendEmail(@RequestBody EmailDto emailDto) {

        String subject = "회원가입 인증 메일입니다.";
        Random random = new Random();
        int code = random.nextInt(9000) + 1000;
        String text = "인증 코드는 " + code + "입니다.";
        memberService.send(emailDto.getEmail(), subject, text, code);
        return new ResponseDto(HttpStatus.OK.value(), "이메일 전송 성공");
    }

    //이메일 인증
    @PostMapping("/api/email/verify")
    public ResponseDto verifyEmail(@RequestBody EmailDto emailDto) {
        String email = emailDto.getEmail();
        String code = emailDto.getCode(); //사용자가 입력한 코드
        String savedCode = memberService.getVerificationCode(email); //redis에 저장된 코드
        memberService.verificationEmail(code, savedCode);
        return new ResponseDto(HttpStatus.OK.value(), "이메일 인증 성공");
    }

    //refresh 토큰 인증 & access 재발급 (재발급할때 refresh도 갱신)
    @PostMapping("/api/auth/refresh")
    public ResponseDto refreshAndGetAuthenticationToken(HttpServletRequest request) throws Exception {
        String authToken = request.getHeader("Authorization");
        final String token = authToken.substring("Bearer ".length());
        String username = jwtTokenUtil.extractUsername(token); //이메일
        String storedRefreshToken = redisTemplate.opsForValue().get(username); //key가 email인 refresh Token 가져옴

        //redis에 저장한 토큰이랑 받은 토큰 비교
        if (storedRefreshToken != null && storedRefreshToken.equals(token)) {
            // 새로운 Access Token 생성
            String refreshedToken = jwtTokenUtil.generateToken(userDetailsService.loadUserByUsername(username));

            // 새로운 Refresh Token 생성 및 Redis에 저장
            String newRefreshToken = jwtTokenUtil.generateRefreshToken(userDetailsService.loadUserByUsername(username));
            redisTemplate.opsForValue().set(username, newRefreshToken);
            redisTemplate.expire(username, jwtTokenUtil.getRefreshExpirationTime(), TimeUnit.MILLISECONDS);

            Map<String, Object> data = new HashMap<>();
            data.put("message", "재발급 성공");
            data.put("token", refreshedToken);
            data.put("refreshToken", newRefreshToken);

            return new ResponseDto<>(HttpStatus.OK.value(), data);
        } else {
            return new ResponseDto(HttpStatus.UNAUTHORIZED.value(), "재발급 실패");
        }
    }

    //로그아웃
    @PostMapping("/api/auth/logout")
    public ResponseDto logout(HttpServletRequest request) {
        String authToken = request.getHeader("Authorization");
        final String token = authToken.substring("Bearer ".length());
        String username = jwtTokenUtil.extractUsername(token); //이메일

        // Redis에서 username에 해당하는 Refresh Token 삭제
        redisTemplate.delete(username);

        return new ResponseDto(HttpStatus.UNAUTHORIZED.value(), "로그아웃 성공");

    }


}
