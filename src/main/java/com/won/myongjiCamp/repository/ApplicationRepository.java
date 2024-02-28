package com.won.myongjiCamp.repository;

import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.application.Application;
import com.won.myongjiCamp.model.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    void deleteByApplicantAndBoard(Member applicant, Board board);

    Optional<Application> findByApplicantAndBoard(Member applicant, Board board);
}
