package com.won.myongjiCamp.repository;

import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.Resume;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume,Long> {
    List<Resume> findByMember(Member member);

}
