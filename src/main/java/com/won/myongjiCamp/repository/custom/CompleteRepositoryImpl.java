package com.won.myongjiCamp.repository.custom;

import static com.won.myongjiCamp.model.board.QCompleteBoard.completeBoard;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.won.myongjiCamp.dto.request.BoardRequest;
import com.won.myongjiCamp.model.board.CompleteBoard;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

public class CompleteRepositoryImpl implements CompleteRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public CompleteRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<CompleteBoard> searchBoards(BoardRequest.BoardSearchDto requestDto, Pageable pageable) {

        JPAQuery<CompleteBoard> query = queryFactory
                .selectFrom(completeBoard)
                .where(
                        withTitleOrContent(requestDto.getKeyword())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        for (Sort.Order o : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(completeBoard.getType(), completeBoard.getMetadata());
            query.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(o.getProperty())));
        }

        List<CompleteBoard> content = query.fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(completeBoard.count())
                .from(completeBoard)
                .where(
                        withTitleOrContent(requestDto.getKeyword())
                );

        return PageableExecutionUtils.getPage(content, pageable,
                countQuery::fetchOne);
    }

    public BooleanExpression withTitleOrContent(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        return completeBoard.title.containsIgnoreCase(keyword)
                .or(completeBoard.content.containsIgnoreCase(keyword));
    }
}
