package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.dto.CreateMemberDto;
import com.won.myongjiCamp.dto.EmailDto;
import com.won.myongjiCamp.dto.ResponseDto;
import com.won.myongjiCamp.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final StringRedisTemplate redisTemplate;

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

}
