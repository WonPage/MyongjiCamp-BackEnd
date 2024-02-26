package com.won.myongjiCamp.repository;

import com.won.myongjiCamp.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume,Long> {
}
