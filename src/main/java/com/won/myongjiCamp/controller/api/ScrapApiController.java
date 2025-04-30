package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.security.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.response.ResponseDto;
import com.won.myongjiCamp.dto.request.ScrapRequest;
import com.won.myongjiCamp.dto.response.ScrapResponse;
import com.won.myongjiCamp.model.Scrap;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.service.ScrapService;
import jakarta.validation.Valid;
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

    @PostMapping("/api/auth/scrap/{boardId}")
    public ResponseDto<String> scrap(@PathVariable Long boardId, @AuthenticationPrincipal PrincipalDetail principal) {
        String data = scrapService.scrap(boardId, principal.getMember());
        return new ResponseDto<String>(HttpStatus.OK.value(), data);
    }

    @GetMapping("/api/auth/scrap")
    public ResponseDto<List<ScrapResponse.BoardListResponse>> getScrapList(@ModelAttribute @Valid ScrapRequest requestDto, @AuthenticationPrincipal PrincipalDetail principal) {
        log.info("Request param: {}", requestDto);

        Page<Scrap> scraps = scrapService.getScrapList(requestDto, principal.getMember());

        List<Board> boards = scraps.stream()
                .map(Scrap::getBoard)
                .toList();

        List<ScrapResponse.BoardListResponse> collect = boards.stream()
                .map(ScrapResponse.BoardListResponse::new)
                .collect(Collectors.toList());

        return new ResponseDto<>(HttpStatus.OK.value(), collect);
    }

    @GetMapping("/api/auth/scrap/{boardId}")
    public ResponseDto<Boolean> isScrap(@PathVariable Long boardId, @AuthenticationPrincipal PrincipalDetail principal) {
        boolean isScrap = scrapService.isScrap(boardId, principal.getMember());
        return new ResponseDto<>(HttpStatus.OK.value(), isScrap);
    }
}
