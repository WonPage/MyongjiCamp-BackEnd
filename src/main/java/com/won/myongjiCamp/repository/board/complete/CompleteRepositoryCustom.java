package com.won.myongjiCamp.repository.board.complete;

import com.won.myongjiCamp.dto.request.BoardRequest.BoardSearchDto;
import com.won.myongjiCamp.model.board.CompleteBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompleteRepositoryCustom {
    Page<CompleteBoard> searchBoards(BoardSearchDto requestDto, Pageable pageable);
}
