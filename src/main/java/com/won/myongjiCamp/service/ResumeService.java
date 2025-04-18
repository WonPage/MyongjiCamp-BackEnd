package com.won.myongjiCamp.service;

import com.won.myongjiCamp.exception.MemberNoMatchException;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.Resume;
import com.won.myongjiCamp.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    
    @Transactional
    public void write(String title, String content, String url, Member member) {
        Resume resume = Resume.builder()
                .title(title)
                .content(content)
                .url(url)
                .member(member)
                .build();

        resumeRepository.save(resume);
    }

    @Transactional
    public void update(String title, String content, String url, long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 이력서가 존재하지 않습니다."));
        resume.setTitle(title);
        resume.setContent(content);
        resume.setUrl(url);
        resume.setCreatedDate(new Timestamp(System.currentTimeMillis()));
    }

    @Transactional
    public void delete(long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 이력서가 존재하지 않습니다."));
        resumeRepository.delete(resume);
    }


    public List<Resume> getListResume(Member member) {
        return resumeRepository.findByMember(member);
    }

    public Resume getDetailResume(long id, Member member) throws MemberNoMatchException {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 이력서가 존재하지 않습니다."));
        if(!member.getId().equals(resume.getMember().getId()))
            throw new MemberNoMatchException("본인 이력서만 열람할 수 있습니다.");
        return resume;
    }
}
