package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.security.auth.PrincipalDetail;
import com.won.myongjiCamp.config.security.auth.PrincipalDetailService;
import com.won.myongjiCamp.config.jwt.JwtTokenUtil;
import com.won.myongjiCamp.dto.request.MemberRequest;
import com.won.myongjiCamp.dto.TokenDto;
import com.won.myongjiCamp.dto.response.MemberResponse;
import com.won.myongjiCamp.dto.response.ResponseDto;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final StringRedisTemplate redisTemplate;
    private final JwtTokenUtil jwtTokenUtil;
    private final PrincipalDetailService detailService;

    @PostMapping("/api/members")
    public ResponseDto saveMember(@RequestBody @Valid MemberRequest.CreateMemberDto request) {
        Long id = memberService.join(request.getEmail(), request.getPassword(), request.getNickname(),
                request.getProfileIcon());
        Map<String, Long> data = new HashMap<>();
        data.put("id", id);

        return new ResponseDto(HttpStatus.OK.value(), data);
    }

    @PostMapping("/api/login")
    public ResponseDto login(@RequestBody @Valid MemberRequest.loginMember request) {
        Member member = memberService.login(request.getUsername(), request.getPassword());

        if (member == null) {
            return new ResponseDto(HttpStatus.BAD_REQUEST.value(), "아이디 또는 비밀번호가 틀렸습니다.");
        }

        PrincipalDetail principal = new PrincipalDetail(member);

        Map<String, String> tokens = memberService.generateAndStoreTokens(principal);

        Map<String, Object> data = new HashMap<>();
        data.put("message", "로그인 성공");
        data.putAll(tokens);

        return new ResponseDto(HttpStatus.OK.value(), data);
    }

    @PostMapping("/api/email")
    public ResponseDto sendEmail(@RequestBody MemberRequest.EmailDto emailDto) {
        String subject = "회원가입 인증 메일입니다.";
        Random random = new Random();
        int code = random.nextInt(9000) + 1000;
        String text = "인증 코드는 " + code + "입니다.";
        memberService.sendCode(emailDto.getEmail(), subject, text, code);

        return new ResponseDto(HttpStatus.OK.value(), "이메일 전송 성공");
    }

    @PostMapping("/api/email/verify")
    public ResponseDto verifyEmail(@RequestBody MemberRequest.EmailDto emailDto) {
        String email = emailDto.getEmail();
        String code = emailDto.getCode();
        String storedCode = memberService.getVerificationCode(email);
        memberService.verificationEmail(code, storedCode);

        return new ResponseDto(HttpStatus.OK.value(), "이메일 인증 성공");
    }

    @PostMapping("/api/refresh")
    public ResponseDto refreshAndGetNewToken(HttpServletRequest request) throws Exception {
        String authToken = request.getHeader("Authorization");
        final String token = authToken.substring("Bearer ".length());
        String email = jwtTokenUtil.extractUsername(token);
        String storedRefreshToken = redisTemplate.opsForValue()
                .get("refresh token:" + email);

        if (storedRefreshToken != null && storedRefreshToken.equals(token)) {
            Map<String, Object> data = getNewTokenAndSaveToRedis(email);
            return new ResponseDto<>(HttpStatus.OK.value(), data);
        }

        return new ResponseDto(HttpStatus.UNAUTHORIZED.value(), "재발급 실패");
    }

    private Map<String, Object> getNewTokenAndSaveToRedis(String email) {
        String refreshedToken = jwtTokenUtil.generateToken((PrincipalDetail) detailService.loadUserByUsername(email));

        String newRefreshToken = jwtTokenUtil.generateRefreshToken(
                (PrincipalDetail) detailService.loadUserByUsername(email));
        redisTemplate.opsForValue().set("refresh token:" + email, newRefreshToken);
        redisTemplate.expire("refresh token:" + email, jwtTokenUtil.getRefreshExpirationTime(), TimeUnit.MILLISECONDS);

        Map<String, Object> data = new HashMap<>();
        data.put("message", "재발급 성공");
        data.put("token", refreshedToken);
        data.put("refreshToken", newRefreshToken);

        return data;
    }

    @PostMapping("/api/auth/logout")
    public ResponseDto logout(HttpServletRequest request, @RequestBody TokenDto expoToken) {
        String authToken = request.getHeader("Authorization");
        final String token = authToken.substring("Bearer ".length());
        String username = jwtTokenUtil.extractUsername(token); //이메일

        redisTemplate.delete("refresh token:" + username);
        redisTemplate.opsForList().remove("expo notification token:" + username, 0, expoToken.getToken());

        return new ResponseDto(HttpStatus.OK.value(), "로그아웃 성공");
    }

    @PostMapping("/api/email/password")
    public ResponseDto findPassword(@RequestBody MemberRequest.EmailDto emailDto) {
        String subject = "임시 비밀번호 안내 이메일 입니다.";
        String password = generateRandomPassword();
        String text = "임시 비밀번호는 " + password + "입니다.\n\n 보안을 위해 빠른 비밀번호 변경을 권장합니다.";
        memberService.sendTemporaryPasswordAndSaveInDB(emailDto.getEmail(), subject, text, password);

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

    @PostMapping("/api/auth/password/verify")
    public ResponseDto verificationBeforeChangePassword(@RequestBody MemberRequest.PasswordDto request,
                                                        @AuthenticationPrincipal PrincipalDetail principal) {
        memberService.verificationPassword(request.getPassword(), principal.getPassword());

        return new ResponseDto(HttpStatus.OK.value(), "비밀번호 인증 성공");
    }

    @PutMapping("/api/auth/password/update")
    public ResponseDto updatePassword(@RequestBody MemberRequest.PasswordDto request,
                                      @AuthenticationPrincipal PrincipalDetail principal) throws Exception {
        memberService.updatePassword(request, principal.getMember());

        return new ResponseDto<>(HttpStatus.OK.value(), "비밀번호 변경이 완료되었습니다.");
    }

    @PutMapping("/api/auth/nickname/update")
    public ResponseDto updateNickname(@RequestBody MemberRequest.ProfileDto request,
                                      @AuthenticationPrincipal PrincipalDetail principal) throws Exception {
        memberService.updateNickname(request, principal.getMember());

        return new ResponseDto<>(HttpStatus.OK.value(), "닉네임 변경이 완료되었습니다.");
    }

    @PutMapping("/api/auth/icon/update")
    public ResponseDto updateIcon(@RequestBody MemberRequest.ProfileIconRequestDto request,
                                  @AuthenticationPrincipal PrincipalDetail principal) throws Exception {
        memberService.updateIcon(request.getProfileIcon(), principal.getMember());

        return new ResponseDto<>(HttpStatus.OK.value(), "아이콘 변경이 완료되었습니다.");
    }

    @GetMapping("/api/auth/profile")
    public Result profile(@AuthenticationPrincipal PrincipalDetail principal) {
        return new Result(new MemberResponse.ProfileInformationResponseDto(principal.getUsername(),
                principal.getMember().getNickname(), principal.getMember().getProfileIcon()));
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
