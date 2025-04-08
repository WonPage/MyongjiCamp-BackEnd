package com.won.myongjiCamp.repository.board.recruit;

import static com.won.myongjiCamp.model.board.QRecruitBoard.recruitBoard;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.won.myongjiCamp.dto.request.BoardRequest;
import com.won.myongjiCamp.model.board.RecruitBoard;
import com.won.myongjiCamp.model.board.RecruitStatus;
import com.won.myongjiCamp.model.board.role.Role;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

public class RecruitRepositoryImpl implements RecruitRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public RecruitRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<RecruitBoard> searchBoards(BoardRequest.BoardSearchDto requestDto, Pageable pageable) {

        JPAQuery<RecruitBoard> query = queryFactory
                .selectFrom(recruitBoard)
                .where(
                        withRoles(requestDto.getRoles()),
                        withTitleOrContent(requestDto.getKeyword()),
                        withStatus(requestDto.getStatus())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        for (Sort.Order o : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(recruitBoard.getType(), recruitBoard.getMetadata());
            query.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(o.getProperty())));
        }

        List<RecruitBoard> content = query.fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(recruitBoard.count())
                .from(recruitBoard)
                .where(
                        withRoles(requestDto.getRoles()),
                        withTitleOrContent(requestDto.getKeyword()),
                        withStatus(requestDto.getStatus())
                );

        return PageableExecutionUtils.getPage(content, pageable,
                countQuery::fetchOne);
    }

    public BooleanExpression withTitleOrContent(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        return recruitBoard.title.containsIgnoreCase(keyword)
                .or(recruitBoard.content.containsIgnoreCase(keyword));
    }

    public BooleanExpression withRoles(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }

        List<Role> roleEnumList = roles.stream()
                .map(Role::valueOf)
                .collect(Collectors.toList());

        return recruitBoard.roles.any().role.in(roleEnumList);
    }

    public BooleanExpression withStatus(String status) {
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
