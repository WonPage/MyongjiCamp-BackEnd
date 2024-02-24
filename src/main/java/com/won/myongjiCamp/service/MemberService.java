package com.won.myongjiCamp.service;

import com.won.myongjiCamp.exception.EmailDuplicatedException;
import com.won.myongjiCamp.exception.NicknameDuplicatedException;
import com.won.myongjiCamp.exception.VerificationFailureException;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
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
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;

    //회원가입
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

    //이메일 전송
    public void send(String email, String subject, String text, int code) {
        long count = getEmailRequestCount(email);
        if (count == 5) {
            throw new RuntimeException("이메일 인증 요청 5번 초과로 24시간 동안 이메일 인증 요청을 할 수 없습니다.");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
        saveVerificationCode(email, String.valueOf(code)); //인증 코드 저장

        increaseEmailRequestCount(email); // 이메일을 보낸 후 요청 횟수를 증가
    }

    //redis에서 인증코드 가져오기
    public String getVerificationCode(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    //redis에 인증코드 저장
    public void saveVerificationCode(String email, String code) {
        redisTemplate.opsForValue().set(email, code, 2, TimeUnit.MINUTES); //2분 타임아웃
    }

    //이메일 요청 카운트 증가
    public void increaseEmailRequestCount(String email) {
        String key = "email_request_count:" + email;
        long count = redisTemplate.opsForValue().increment(key);

        if (count == 5) {
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
        }
    }

    //이메일 요청 카운트 가져오기
    public long getEmailRequestCount(String email) {
        String key = "email_request_count:" + email;
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0;
    }

    //이메일 인증
    public void verificationEmail(String code, String savedCode) {
        if (!code.equals(savedCode)) {
            throw new VerificationFailureException("이메일 인증 실패");
        }
    }
}
