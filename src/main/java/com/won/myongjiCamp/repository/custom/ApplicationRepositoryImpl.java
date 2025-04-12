package com.won.myongjiCamp.repository.custom;

import static com.won.myongjiCamp.model.QMember.member;
import static com.won.myongjiCamp.model.application.QApplication.application;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.won.myongjiCamp.model.application.Application;
import com.won.myongjiCamp.model.board.Board;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ApplicationRepositoryImpl implements ApplicationRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public ApplicationRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Application> findByBoard(Board board) {

        return queryFactory
                .selectFrom(application)
                .join(application.applicant, member)
                .fetchJoin()
                .where(application.board.eq(board))
                .fetch();
    }
}
