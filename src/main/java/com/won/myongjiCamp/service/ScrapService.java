package com.won.myongjiCamp.service;

import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.Scrap;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.repository.BoardRepository;
import com.won.myongjiCamp.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
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
}
