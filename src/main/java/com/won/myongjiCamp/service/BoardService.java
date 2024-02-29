package com.won.myongjiCamp.service;

import com.won.myongjiCamp.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

//    public void findAll(Sort) {
//        boardRepository.findAll();
//    }
}
