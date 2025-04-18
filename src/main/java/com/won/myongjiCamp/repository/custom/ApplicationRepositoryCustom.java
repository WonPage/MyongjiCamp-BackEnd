package com.won.myongjiCamp.repository.custom;

import com.won.myongjiCamp.model.application.Application;
import com.won.myongjiCamp.model.board.Board;
import java.util.List;

public interface ApplicationRepositoryCustom {
    List<Application> findByBoard(Board board);
}
