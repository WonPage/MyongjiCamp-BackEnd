package com.won.myongjiCamp.service;

import com.won.myongjiCamp.dto.request.BoardRequest.BoardSearchDto;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.board.CompleteBoard;
import com.won.myongjiCamp.model.board.RecruitBoard;
import com.won.myongjiCamp.repository.board.complete.CompleteRepository;
import com.won.myongjiCamp.repository.board.recruit.RecruitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final CompleteRepository completeRepository;
    private final RecruitRepository recruitRepository;

    public List<CompleteBoard> listMemberComplete(Member member) {
        List boards = completeRepository.findByMember(member);
        return boards;
    }

    public Page<RecruitBoard> searchRecruitBoards(BoardSearchDto requestDto) {
        Pageable pageable = getPageable(requestDto);
        return recruitRepository.searchBoards(requestDto, pageable);
    }

    public Page<CompleteBoard> searchCompleteBoards(BoardSearchDto requestDto) {
        Pageable pageable = getPageable(requestDto);
        return completeRepository.searchBoards(requestDto, pageable);
    }

    private static Pageable getPageable(BoardSearchDto requestDto) {
        Sort.Direction direction;
        if(requestDto.getDirection().equals("DESC")) {
            direction = Sort.Direction.DESC;
        } else{
            direction = Sort.Direction.ASC;
        }

        Pageable pageable = PageRequest.of(requestDto.getPageNum(), 8, Sort.by(direction, "modifiedDate"));
        return pageable;
    }
}
