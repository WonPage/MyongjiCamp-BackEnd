package com.won.myongjiCamp.service;

import com.won.myongjiCamp.dto.request.BoardSearchDto;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    public Page<Board> searchBoards(BoardSearchDto requestDto) {

        Sort.Direction direction;
        if(requestDto.getDirection().equals("DESC")) {
            direction = Sort.Direction.DESC;
        } else{
            direction = Sort.Direction.ASC;
        }
        String property = "modifiedDate";
        String decodedKeyword = URLDecoder.decode(requestDto.getKeyword(), StandardCharsets.UTF_8);
        Pageable pageable = PageRequest.of(requestDto.getPageNum(), 8, Sort.by(direction, property));

        Specification<Board> spec = Specification.where(BoardSpecification.withRoles(requestDto.getRoles()))
                .and(BoardSpecification.withTitleOrContent(decodedKeyword))
                .and(BoardSpecification.withStatus(requestDto.getStatus()))
                .and(BoardSpecification.withBoardType(requestDto.getBoardType()));

        return boardRepository.findAll(spec, pageable);
    }

}
