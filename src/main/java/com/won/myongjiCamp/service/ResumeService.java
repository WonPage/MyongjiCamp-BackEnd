package com.won.myongjiCamp.service;

import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.Resume;
import com.won.myongjiCamp.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    
    //이력서 작성
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
    //이력서 수정
    @Transactional
    public void update(String title, String content, String url, long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 이력서가 존재하지 않습니다."));
        resume.setTitle(title);
        resume.setContent(content);
        resume.setUrl(url);
    }
    //이력서 삭제
    @Transactional
    public void delete(long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 이력서가 존재하지 않습니다."));
        resumeRepository.delete(resume);
    }
}
