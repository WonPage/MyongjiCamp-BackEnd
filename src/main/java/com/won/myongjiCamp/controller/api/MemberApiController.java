package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.auth.PrincipalDetail;
import com.won.myongjiCamp.config.auth.PrincipalDetailService;
import com.won.myongjiCamp.config.jwt.JwtTokenUtil;
import com.won.myongjiCamp.dto.request.CreateMemberDto;
import com.won.myongjiCamp.dto.request.EmailDto;
import com.won.myongjiCamp.dto.ResponseDto;
import com.won.myongjiCamp.dto.request.PasswordDto;
import com.won.myongjiCamp.dto.request.ProfileDto;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.repository.MemberRepository;
import com.won.myongjiCamp.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final StringRedisTemplate redisTemplate;

    private final JwtTokenUtil jwtTokenUtil;
    private final PrincipalDetailService detailService;

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
        memberService.sendCode(emailDto.getEmail(), subject, text, code);
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
    @PostMapping("/api/refresh")
    public ResponseDto refreshAndGetAuthenticationToken(HttpServletRequest request) throws Exception {
        String authToken = request.getHeader("Authorization");
        final String token = authToken.substring("Bearer ".length());
        String email = jwtTokenUtil.extractUsername(token); //이메일
        String storedRefreshToken = redisTemplate.opsForValue().get("refresh token:" + email); //key가 email인 refresh Token 가져옴

        //redis에 저장한 토큰이랑 받은 토큰 비교
        if (storedRefreshToken != null && storedRefreshToken.equals(token)) {
            Map<String, Object> data = newToken(email);

            return new ResponseDto<>(HttpStatus.OK.value(), data);
        } else {
            return new ResponseDto(HttpStatus.UNAUTHORIZED.value(), "재발급 실패");
        }
    }

    //새로운 accsss, refresh 발급받고 redis에 저장
    private Map<String, Object> newToken(String email) {
        // 새로운 Access Token 생성
        String refreshedToken = jwtTokenUtil.generateToken((PrincipalDetail) detailService.loadUserByUsername(email));

        // 새로운 Refresh Token 생성 및 Redis에 저장
        String newRefreshToken = jwtTokenUtil.generateRefreshToken((PrincipalDetail) detailService.loadUserByUsername(email));
        redisTemplate.opsForValue().set("refresh token:" + email, newRefreshToken);
        redisTemplate.expire("refresh token:" + email, jwtTokenUtil.getRefreshExpirationTime(), TimeUnit.MILLISECONDS);

        Map<String, Object> data = new HashMap<>();
        data.put("message", "재발급 성공");
        data.put("token", refreshedToken);
        data.put("refreshToken", newRefreshToken);
        return data;
    }

    //로그아웃
    @PostMapping("/api/auth/logout")
    public ResponseDto logout(HttpServletRequest request) {
        String authToken = request.getHeader("Authorization");
        final String token = authToken.substring("Bearer ".length());
        String username = jwtTokenUtil.extractUsername(token); //이메일

        // Redis에서 username에 해당하는 Refresh Token 삭제
        redisTemplate.delete("refresh token:" + username);

        return new ResponseDto(HttpStatus.OK.value(), "로그아웃 성공");
    }

    //비밀번호 찾기(임시 비밀번호 발급)
    @PostMapping("/api/email/password")
    public ResponseDto findPassword(@RequestBody EmailDto emailDto) {

        String subject = "임시 비밀번호 안내 이메일 입니다.";
        String password = generateRandomPassword();
        String text = "임시 비밀번호는 " + password + "입니다.\n\n 보안을 위해 빠른 비밀번호 변경을 권장합니다.";
        memberService.sendPassword(emailDto.getEmail(), subject, text, password);
        return new ResponseDto(HttpStatus.OK.value(), "이메일 전송 성공");
    }

    public String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";

        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }

        return password.toString();
    }

    //비밀번호 변경 전 현재 비밀번호 인증
    @PostMapping("/api/auth/password/verify")
    public ResponseDto verificationPassword(@RequestBody PasswordDto request, @AuthenticationPrincipal PrincipalDetail principal) {
        memberService.verificationPassword(request.getPassword(),principal.getPassword());
        return new ResponseDto(HttpStatus.OK.value(), "비밀번호 인증 성공");
    }

    //개인정보(비밀번호 변경)
    @PutMapping("/api/auth/password/update")
    public ResponseDto updatePassword(@RequestBody PasswordDto request, @AuthenticationPrincipal PrincipalDetail principal) throws Exception {
        memberService.updatePassword(request,principal.getMember());
        return new ResponseDto<>(HttpStatus.OK.value(), "비밀번호 변경이 완료되었습니다.");
    }

    //닉네임 변경
    @PutMapping("/api/auth/nickname/update")
    public ResponseDto updateNickname(@RequestBody ProfileDto request, @AuthenticationPrincipal PrincipalDetail principal) throws Exception {
        memberService.updateNickname(request,principal.getMember());
        return new ResponseDto<>(HttpStatus.OK.value(), "닉네임 변경이 완료되었습니다.");
    }

    //아이콘 변경
    @PutMapping("/api/auth/icon/update")
    public ResponseDto updateIcon(@RequestBody ProfileIconRequestDto request, @AuthenticationPrincipal PrincipalDetail principal) throws Exception {
        memberService.updateIcon(request.getProfileIcon(),principal.getMember());
        return new ResponseDto<>(HttpStatus.OK.value(), "아이콘 변경이 완료되었습니다.");
    }

    //프로필
    @GetMapping("/api/auth/profile")
    public Result profile(@AuthenticationPrincipal PrincipalDetail principal) {
        return new Result(new ProfileInformationResponseDto(principal.getUsername(),principal.getMember().getNickname(), principal.getMember().getProfileIcon()));
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class ProfileInformationResponseDto {
        private String email;
        private String nickname;
        private Integer profileIcon;
    }

    @Data
    static class ProfileIconRequestDto {
        private Integer profileIcon;
    }
}
