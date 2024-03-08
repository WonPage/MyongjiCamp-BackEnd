package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.BoardListResponseDto;
import com.won.myongjiCamp.dto.CommentResponseDto;
import com.won.myongjiCamp.dto.ResponseDto;
import com.won.myongjiCamp.dto.request.ScrapDto;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.Scrap;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.CompleteBoard;
import com.won.myongjiCamp.model.board.RecruitBoard;
import com.won.myongjiCamp.model.board.RecruitStatus;
import com.won.myongjiCamp.model.board.role.Role;
import com.won.myongjiCamp.model.board.role.RoleAssignment;
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

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ScrapApiController {

    final private ScrapService scrapService;
    final private MemberRepository memberRepository;
//    @PostMapping("/api/auth/scrap/{id}")
//    public ResponseDto<String> scrap(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetail principal) {
//        String data = scrapService.scrap(id, principal.getMember());
//        return new ResponseDto<String>(HttpStatus.OK.value(), data);
//    }

    //스크랩 테스트
    @PostMapping("/api/auth/scrap/{id}")
    public ResponseDto<String> scrap(@PathVariable Long id) {
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
        String data = scrapService.scrap(id, member);
        return new ResponseDto<String>(HttpStatus.OK.value(), data);
    }

    //스크랩 가져오기
    @GetMapping("/api/auth/scrap")
    public Result pullScraps(@ModelAttribute @Valid ScrapDto requestDto, @AuthenticationPrincipal PrincipalDetail principal) {
        log.info("Request param: {}", requestDto);

        Page<Scrap> scraps = scrapService.pullScraps(requestDto, principal.getMember());

        List<Board> boards = scraps.stream()
                .map(Scrap::getBoard)
                .collect(Collectors.toList());

        List<BoardListResponseDto> collect = boards.stream()
                .map(BoardListResponseDto::new)
                .collect(Collectors.toList());

        return new Result(collect);
    }

//    //스크랩 가져오기 테스트
//    @GetMapping("/api/auth/scrap")
//    public Result pullScraps(@ModelAttribute @Valid ScrapDto requestDto) {
//        Member member = memberRepository.findById(1L)
//                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
//
//
//        Page<Scrap> scraps = scrapService.pullScraps(requestDto, member);
//
//        List<Board> boards = scraps.stream()
//                .map(Scrap::getBoard)
//                .collect(Collectors.toList());
//
//        List<BoardListResponseDto> collect = boards.stream()
//                .map(BoardListResponseDto::new)
//                .collect(Collectors.toList());
//
//        return new Result(collect);
//    }
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
