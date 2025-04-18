package com.won.myongjiCamp.service;

import com.won.myongjiCamp.dto.request.ScrapRequest;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.Scrap;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.repository.BoardRepository;
import com.won.myongjiCamp.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScrapService  {

    private final ScrapRepository scrapRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public String scrap(Long id, Member member) {

        String data;
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 글이 존재하지 않습니다."));

        if(!scrapRepository.existsByMemberAndBoard(member, board)) {
            Scrap scrap = Scrap.builder()
                    .member(member)
                    .board(board)
                    .build();
            scrapRepository.save(scrap);
            board.setScrapCount(board.getScrapCount() + 1);
            data = "스크랩 완료";
            return data;
        }

        scrapRepository.deleteByMemberAndBoard(member, board);
        board.setScrapCount(board.getScrapCount() - 1);
        data = "스크랩 취소";
        return data;
    }

    public Page<Scrap> getScrapList(ScrapRequest requestDto, Member member) {

        Pageable pageable = PageRequest.of(requestDto.getPageNum(), 8);

        return scrapRepository.searchScrapBoards(requestDto, pageable, member);
    }

    public boolean isScrap(Long boardId, Member member) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 글이 존재하지 않습니다."));

        return scrapRepository.existsByMemberAndBoard(member, board);
    }
}
