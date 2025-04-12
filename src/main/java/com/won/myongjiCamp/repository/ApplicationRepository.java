package com.won.myongjiCamp.repository;

import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.application.Application;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.repository.custom.ApplicationRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long>, ApplicationRepositoryCustom {
    void deleteByApplicantAndBoard(Member applicant, Board board);

    Optional<Application> findByApplicantAndBoard(Member applicant, Board board);

    List<Application> findByApplicant(Member applicant);

    long countByBoard(Board board);
}
