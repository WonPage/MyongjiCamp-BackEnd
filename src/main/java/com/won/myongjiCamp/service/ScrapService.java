package com.won.myongjiCamp.service;

import com.won.myongjiCamp.dto.request.ScrapRequest;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.Scrap;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.repository.BoardRepository;
import com.won.myongjiCamp.repository.MemberRepository;
import com.won.myongjiCamp.repository.ScrapRepository;
import com.won.myongjiCamp.specification.ScrapSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScrapService  {

    private final ScrapRepository scrapRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public String scrap(Long id, Member member) {

        String data;
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 글이 존재하지 않습니다."));

        //이미 스크랩 했는지 확인
        if(!scrapRepository.existsByMemberAndBoard(member, board)) {
            Scrap scrap = Scrap.builder()
                    .member(member)
                    .board(board)
                    .build();
            scrapRepository.save(scrap);
            board.setScrapCount(board.getScrapCount() + 1);
            data = "스크랩 완료";
            return data;
        } else {
            scrapRepository.deleteByMemberAndBoard(member, board);
            board.setScrapCount(board.getScrapCount() - 1);
            data = "스크랩 취소";
            return data;
        }

    }

    public Page<Scrap> pullScraps(ScrapRequest requestDto, Member member) {

        Member findMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
        String property = "createdDate";
        Pageable pageable = PageRequest.of(requestDto.getPageNum(), 8, Sort.by(Sort.Direction.DESC, property));

        Specification<Scrap> spec = Specification.where(ScrapSpecification.withStatus(requestDto.getStatus()))
                .and(ScrapSpecification.withBoardType(requestDto.getBoardType()))
                .and(ScrapSpecification.withMember(findMember));

        return scrapRepository.findAll(spec, pageable);
    }

    public boolean isScrap(Long board_id, Member member) {
        Board board = boardRepository.findById(board_id)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 글이 존재하지 않습니다."));

        if(!scrapRepository.existsByMemberAndBoard(member, board)) {
            return false;
        }
        else{
            return true;
        }
    }
}
