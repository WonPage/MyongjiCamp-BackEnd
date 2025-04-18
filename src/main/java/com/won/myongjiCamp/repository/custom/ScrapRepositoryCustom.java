package com.won.myongjiCamp.repository.custom;

import com.won.myongjiCamp.dto.request.ScrapRequest;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.Scrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScrapRepositoryCustom {
    Page<Scrap> searchScrapBoards(ScrapRequest requestDto, Pageable pageable, Member member);
}
