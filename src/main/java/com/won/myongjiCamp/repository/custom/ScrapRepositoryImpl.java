package com.won.myongjiCamp.repository.custom;

import static com.won.myongjiCamp.model.QScrap.scrap;
import static com.won.myongjiCamp.model.board.QBoard.board;
import static com.won.myongjiCamp.model.board.QRecruitBoard.recruitBoard;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.won.myongjiCamp.dto.request.ScrapRequest;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.Scrap;
import com.won.myongjiCamp.model.board.CompleteBoard;
import com.won.myongjiCamp.model.board.RecruitBoard;
import com.won.myongjiCamp.model.board.RecruitStatus;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class ScrapRepositoryImpl implements ScrapRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ScrapRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Scrap> searchScrapBoards(ScrapRequest requestDto, Pageable pageable, Member member) {
        JPAQuery<Scrap> query = queryFactory
                .selectFrom(scrap)
                .join(scrap.board, board)
                .fetchJoin()
                .where(
                        withStatus(requestDto.getStatus()),
                        withBoardType(requestDto.getBoardType()),
                        scrap.member.eq(member)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(scrap.createdDate.desc());

        if ("recruit".equals(requestDto.getBoardType())) {
            query.leftJoin(recruitBoard).on(recruitBoard.id.eq(scrap.board.id));
        }

        List<Scrap> content = query.fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(scrap.count())
                .from(scrap)
                .where(
                        withStatus(requestDto.getStatus()),
                        withBoardType(requestDto.getBoardType()),
                        scrap.member.eq(member)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression withBoardType(String boardType) {
        if (boardType == null || boardType.isEmpty()) {
            return null;
        }

        PathBuilder<Object> pathBuilder = new PathBuilder<>(scrap.board.getType(), "board");

        if ("recruit".equals(boardType)) {
            return pathBuilder.get("class").eq(RecruitBoard.class);
        }

        if ("complete".equals(boardType)) {
            return pathBuilder.get("class").eq(CompleteBoard.class);
        }

        return null;
    }

    private BooleanExpression withStatus(String status) {
        if (status == null || status.isEmpty()) {
            return null;
        }

        if ("ongoing".equals(status)) {
            return recruitBoard.status.eq(RecruitStatus.RECRUIT_ONGOING);
        }
        if ("complete".equals(status)) {
            return recruitBoard.status.eq(RecruitStatus.RECRUIT_COMPLETE);
        }
        return null;
    }
}
