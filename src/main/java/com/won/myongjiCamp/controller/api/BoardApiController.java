package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.security.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.request.BoardRequest;
import com.won.myongjiCamp.dto.response.BoardResponse;
import com.won.myongjiCamp.dto.response.ResponseDto;
import com.won.myongjiCamp.model.board.*;
import com.won.myongjiCamp.model.board.role.RoleAssignment;
import com.won.myongjiCamp.repository.*;
import com.won.myongjiCamp.repository.board.complete.CompleteRepository;
import com.won.myongjiCamp.repository.board.recruit.RecruitRepository;
import com.won.myongjiCamp.service.BoardService;
import com.won.myongjiCamp.service.CompleteService;
import com.won.myongjiCamp.service.RecruitService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BoardApiController {

    private final RecruitService recruitService;
    private final CompleteService completeService;
    private final BoardService boardService;
    private final RoleAssignmentRepository roleAssignmentRepository;
    private final RecruitRepository recruitRepository;
    private final CompleteRepository completeRepository;

    // 게시글 작성
    @PostMapping("/api/auth/recruit")
    public ResponseDto<String> createRecruit(@RequestBody @Valid BoardRequest.RecruitDto recruitDto, @AuthenticationPrincipal PrincipalDetail principal){
        recruitService.create(recruitDto,principal.getMember());
        return new ResponseDto<String>(HttpStatus.OK.value(),"게시글 작성 완료");
    }

    // recruit 게시글 수정, id는 게시글 id
    @PutMapping("/api/auth/recruit/{id}")
    public ResponseDto<String> updateRecruit(@RequestBody @Valid BoardRequest.RecruitDto recruitDto, @PathVariable long id){
        recruitService.update(recruitDto,id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "게시글이 수정되었습니다.");
    }

    // 모집 게시글 삭제, id는 게시글 id
    @DeleteMapping("/api/auth/recruit/{id}")
    public ResponseDto<String> deleteRecruit(@PathVariable long id){
        recruitService.delete(id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "게시글이 삭제되었습니다.");
    }

    // complete 게시글 작성, id는 모집완료 글 id
    @PostMapping("/api/auth/complete/{id}")
    public Result createComplete(@ModelAttribute @Valid BoardRequest.CompleteDto completeDto, @AuthenticationPrincipal PrincipalDetail principalDetail, @PathVariable long id) throws IOException {

        CompleteService.WriteCompleteResponseDto writeCompleteResponseDto = completeService.create(completeDto, principalDetail.getMember(), id);

        return new Result(writeCompleteResponseDto);
    }


    // complete 게시글 수정, id는 게시글 id
    @PutMapping("/api/auth/complete/{id}")
    public Result updateComplete(@ModelAttribute @Valid BoardRequest.CompleteDto completeDto, @PathVariable long id) throws IOException {
        CompleteService.WriteCompleteResponseDto writeCompleteResponseDto = completeService.update(id, completeDto);
        return new Result(writeCompleteResponseDto);
    }

    // complete 게시글 삭제, id는 게시글 id
    @DeleteMapping("/api/auth/complete/{id}")
    public ResponseDto<String> deleteComplete(@PathVariable long id){
        completeService.delete(id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "게시글이 삭제되었습니다.");
    }

    //글 조회(검색)
    @GetMapping("/api/board")
    public Result findAll(@ModelAttribute @Valid BoardRequest.BoardSearchDto requestDto) {
        if (requestDto.getBoardType().equals("recruit")) {
            Page<RecruitBoard> recruitBoards = boardService.searchRecruitBoards(requestDto);
            List<BoardResponse.RecruitBoardListResponseDto> collect = recruitBoards.stream()
                    .map(BoardResponse.RecruitBoardListResponseDto::new)
                    .collect(Collectors.toList());
            return new Result(collect);
        }
        if (requestDto.getBoardType().equals("complete")) {
            Page<CompleteBoard> completeBoards = boardService.searchCompleteBoards(requestDto);
            List<BoardResponse.CompleteBoardListResponseDto> collect = completeBoards.stream()
                    .map(BoardResponse.CompleteBoardListResponseDto::new)
                    .collect(Collectors.toList());
            return new Result(collect);
        }

        return new Result(List.of());
    }

    // 게시글 상세 읽기
    @GetMapping("/api/recruit/{id}")
    public Result getRecruitDetail(@AuthenticationPrincipal PrincipalDetail principalDetail, @PathVariable Long id){

        RecruitBoard board = recruitRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if(principalDetail == null){ // 로그인 x -> 내용, 제목, 닉네임, 아이콘 보임
//            return ResponseEntity.ok(new Result(new NotDetailRecruitResponseDto(recruitBoard));
            return new Result(new BoardResponse.NotDetailRecruitResponseDto(board));
        }
        else{ // 로그인 o
            List<RoleAssignment> roleAssignmentsList = roleAssignmentRepository.findByBoard(board);
            List<BoardResponse.RoleAssignmentDto> roleDto = new ArrayList<>();
            roleAssignmentsList.stream().forEach(r->{
                roleDto.add(convertRoleToDto(r));
            });

            return new Result(new BoardResponse.DetailRecruitResponseDto(board,roleDto));
        }
    }

    // complete 게시글 상세보기
    @GetMapping("/api/complete/{id}")
    public Result getCompleteDetail(@PathVariable Long id){

        CompleteBoard board = completeRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        return new Result(new BoardResponse.DetailCompleteResponseDto(board));
    }

    // 내가 작성한 complete 게시글 목록
    @GetMapping("/api/auth/complete/writer")
    public Result getMemberComplete(@AuthenticationPrincipal PrincipalDetail principalDetail){

        List<CompleteBoard> boards = boardService.listMemberComplete(principalDetail.getMember());
        List<BoardResponse.MemberCompleteBoardListResponseDto> collect = boards.stream()
                .map(b -> new BoardResponse.MemberCompleteBoardListResponseDto(b))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    public BoardResponse.RoleAssignmentDto convertRoleToDto(RoleAssignment roleAssignment){
        return new BoardResponse.RoleAssignmentDto(
                roleAssignment.getRole(),
                roleAssignment.getAppliedNumber(),
                roleAssignment.getRequiredNumber()
        );
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
