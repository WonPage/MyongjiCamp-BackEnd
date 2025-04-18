package com.won.myongjiCamp.service;

import com.won.myongjiCamp.config.jwt.JwtTokenUtil;
import com.won.myongjiCamp.config.security.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.request.MemberRequest;
import com.won.myongjiCamp.exception.EmailDuplicatedException;
import com.won.myongjiCamp.exception.NicknameDuplicatedException;
import com.won.myongjiCamp.exception.VerificationFailureException;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.RoleType;
import com.won.myongjiCamp.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private final JwtTokenUtil jwtTokenUtil;

    @Transactional
    public Long join(String email, String password, String nickname, Integer icon) throws EmailDuplicatedException, NicknameDuplicatedException {

        if (isEmailDuplicated(email)) {
            throw new EmailDuplicatedException("이미 사용중인 이메일입니다.");
        }
        if (isNicknameDuplicated(nickname)) {
            throw new NicknameDuplicatedException("이미 사용중인 닉네임입니다.");
        }

        String encPassword = bCryptPasswordEncoder.encode(password);//해쉬 비밀번호
        Member member = Member.builder()
                .email(email)
                .password(encPassword)
                .nickname(nickname)
                .profileIcon(icon)
                .role(RoleType.USER)
                .build();
        memberRepository.save(member);
        redisTemplate.delete("email_request_count:" + email);
        return member.getId();
    }

    public boolean isEmailDuplicated(String email) {
        return memberRepository.existsByEmail(email);
    }

    public boolean isNicknameDuplicated(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @Transactional
    public void sendCode(String email, String subject, String text, int code) {
        long count = getEmailRequestCount(email);
        if (count == 5) {
            throw new RuntimeException("이메일 인증 요청 5번 초과로 24시간 동안 이메일 인증 요청을 할 수 없습니다.");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
        saveVerificationCode(email, String.valueOf(code));

        increaseEmailRequestCount(email);
    }

    public String getVerificationCode(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    public void saveVerificationCode(String email, String code) {
        redisTemplate.opsForValue().set(email, code, 2, TimeUnit.MINUTES); //2분 타임아웃
    }

    public void increaseEmailRequestCount(String email) {
        String key = "email_request_count:" + email;
        long count = redisTemplate.opsForValue().increment(key);

        if (count == 5) {
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
        }
    }

    public long getEmailRequestCount(String email) {
        String key = "email_request_count:" + email;
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0;
    }

    public void verificationEmail(String code, String savedCode) {
        if (!code.equals(savedCode)) {
            throw new VerificationFailureException("이메일 인증 실패");
        }
    }

    @Transactional
    public void sendTemporaryPasswordAndSaveInDB(String email, String subject, String text, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("해당 이메일로 가입된 유저가 존재하지 않습니다."));
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
        String encPassword = bCryptPasswordEncoder.encode(password);
        member.setPassword(encPassword);
    }

    public void verificationPassword(String inputPassword, String storedPassword) {
        if(!bCryptPasswordEncoder.matches(inputPassword, storedPassword)){
            throw new IllegalArgumentException("현재 비밀번호와 입력하신 비밀번호가 일치하지 않습니다.");
        }
    }

    @Transactional
    public void updatePassword(MemberRequest.PasswordDto request, Member member) {
        String encPassword = bCryptPasswordEncoder.encode(request.getPassword());
        Member findMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalStateException("해당 유저가 존재하지 않습니다."));
        findMember.setPassword(encPassword);
    }

    @Transactional
    public void updateNickname(MemberRequest.ProfileDto request, Member member) {
        if (isNicknameDuplicated(request.getNickname())) {
            throw new NicknameDuplicatedException("이미 사용중인 닉네임입니다.");
        }
        Member findMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalStateException("해당 유저가 존재하지 않습니다."));
        findMember.setNickname(request.getNickname());
    }

    @Transactional
    public void updateIcon(Integer icon, Member member) {
        Member findMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalStateException("해당 유저가 존재하지 않습니다."));
        findMember.setProfileIcon(icon);
    }

    @Transactional
    public Member login(String email, String password) {
        Optional<Member> optionalEmail = memberRepository.findByEmail(email);

        if (optionalEmail.isEmpty()) {
            return null;
        }

        Member member = optionalEmail.get();

        if (!bCryptPasswordEncoder.matches(password, member.getPassword())) {
            return null;
        }

        return member;
    }

    public Map<String, String> generateAndStoreTokens(PrincipalDetail principal) {
        String accessToken = jwtTokenUtil.generateToken(principal);
        String refreshToken = jwtTokenUtil.generateRefreshToken(principal);

        String email = principal.getUsername();

        redisTemplate.opsForValue().set("refresh token:" + email, refreshToken);
        redisTemplate.expire("refresh token:" + email,
                jwtTokenUtil.getRefreshExpirationTime(), TimeUnit.MILLISECONDS);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("token", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }
}
