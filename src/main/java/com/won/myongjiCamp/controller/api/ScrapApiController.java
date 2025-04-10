package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.security.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.response.ResponseDto;
import com.won.myongjiCamp.dto.request.ScrapRequest;
import com.won.myongjiCamp.dto.response.ScrapResponse;
import com.won.myongjiCamp.model.Scrap;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.repository.MemberRepository;
import com.won.myongjiCamp.service.ScrapService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ScrapApiController {

    final private ScrapService scrapService;
    final private MemberRepository memberRepository;

    @PostMapping("/api/auth/scrap/{id}")
    public ResponseDto<String> scrap(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetail principal) {
        String data = scrapService.scrap(id, principal.getMember());
        return new ResponseDto<String>(HttpStatus.OK.value(), data);
    }

    //스크랩 가져오기
    @GetMapping("/api/auth/scrap")
    public Result pullScraps(@ModelAttribute @Valid ScrapRequest requestDto, @AuthenticationPrincipal PrincipalDetail principal) {
        log.info("Request param: {}", requestDto);

        Page<Scrap> scraps = scrapService.pullScraps(requestDto, principal.getMember());

        List<Board> boards = scraps.stream()
                .map(Scrap::getBoard)
                .collect(Collectors.toList());

        List<ScrapResponse.BoardListResponse> collect = boards.stream()
                .map(ScrapResponse.BoardListResponse::new)
                .collect(Collectors.toList());

        return new Result(collect);
    }

    //스크랩 여부
    @GetMapping("/api/auth/scrap/{board_id}")
    public Result isScrap(@PathVariable Long board_id, @AuthenticationPrincipal PrincipalDetail principal) {
        boolean isScrap = scrapService.isScrap(board_id, principal.getMember());
        return new Result(isScrap);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
