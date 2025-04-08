package com.won.myongjiCamp.repository.board.recruit;

import com.won.myongjiCamp.dto.request.BoardRequest.BoardSearchDto;
import com.won.myongjiCamp.model.board.RecruitBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecruitRepositoryCustom {
    Page<RecruitBoard> searchBoards(BoardSearchDto requestDto, Pageable pageable);
}
