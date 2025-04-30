package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.security.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.request.BoardRequest;
import com.won.myongjiCamp.dto.response.BoardResponse;
import com.won.myongjiCamp.dto.response.ResponseDto;
import com.won.myongjiCamp.model.board.*;
import com.won.myongjiCamp.model.board.role.RoleAssignment;
import com.won.myongjiCamp.repository.*;
import com.won.myongjiCamp.repository.CompleteRepository;
import com.won.myongjiCamp.repository.RecruitRepository;
import com.won.myongjiCamp.service.BoardService;
import com.won.myongjiCamp.service.CompleteService;
import com.won.myongjiCamp.service.RecruitService;
import jakarta.validation.Valid;
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

    @PostMapping("/api/auth/recruit")
    public ResponseDto<String> createRecruit(@RequestBody @Valid BoardRequest.RecruitDto recruitDto, @AuthenticationPrincipal PrincipalDetail principal){
        recruitService.create(recruitDto,principal.getMember());
        return new ResponseDto<String>(HttpStatus.OK.value(),"게시글 작성 완료");
    }

    @PutMapping("/api/auth/recruit/{id}")
    public ResponseDto<String> updateRecruit(@RequestBody @Valid BoardRequest.RecruitDto recruitDto, @PathVariable long id){
        recruitService.update(recruitDto,id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "게시글이 수정되었습니다.");
    }

    @DeleteMapping("/api/auth/recruit/{id}")
    public ResponseDto<String> deleteRecruit(@PathVariable long id){
        recruitService.delete(id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "게시글이 삭제되었습니다.");
    }

    @PostMapping("/api/auth/complete/{id}")
    public ResponseDto<BoardResponse.WriteCompleteResponseDto> createComplete(@ModelAttribute @Valid BoardRequest.CompleteDto completeDto, @AuthenticationPrincipal PrincipalDetail principalDetail, @PathVariable long id) throws IOException {
        BoardResponse.WriteCompleteResponseDto writeCompleteResponseDto = completeService.create(completeDto, principalDetail.getMember(), id);
        return new ResponseDto<>(HttpStatus.OK.value(), writeCompleteResponseDto);
    }

    @PutMapping("/api/auth/complete/{id}")
    public ResponseDto<BoardResponse.WriteCompleteResponseDto> updateComplete(@ModelAttribute @Valid BoardRequest.CompleteDto completeDto, @PathVariable long id) throws IOException {
        BoardResponse.WriteCompleteResponseDto writeCompleteResponseDto = completeService.update(id, completeDto);
        return new ResponseDto<>(HttpStatus.OK.value(), writeCompleteResponseDto);
    }

    @DeleteMapping("/api/auth/complete/{id}")
    public ResponseDto<String> deleteComplete(@PathVariable long id){
        completeService.delete(id);
        return new ResponseDto<>(HttpStatus.OK.value(), "게시글이 삭제되었습니다.");
    }

    @GetMapping("/api/board")
    public ResponseDto searchBoards(@ModelAttribute @Valid BoardRequest.BoardSearchDto requestDto) {
        if (requestDto.getBoardType().equals("recruit")) {
            Page<RecruitBoard> recruitBoards = boardService.searchRecruitBoards(requestDto);
            List<BoardResponse.RecruitBoardListResponseDto> collect = recruitBoards.stream()
                    .map(BoardResponse.RecruitBoardListResponseDto::new)
                    .collect(Collectors.toList());

            return new ResponseDto<>(HttpStatus.OK.value(), collect);
        }

        if (requestDto.getBoardType().equals("complete")) {
            Page<CompleteBoard> completeBoards = boardService.searchCompleteBoards(requestDto);
            List<BoardResponse.CompleteBoardListResponseDto> collect = completeBoards.stream()
                    .map(BoardResponse.CompleteBoardListResponseDto::new)
                    .collect(Collectors.toList());

            return new ResponseDto<>(HttpStatus.OK.value(), collect);
        }

        return new ResponseDto<>(HttpStatus.OK.value(), List.of());
    }

    @GetMapping("/api/recruit/{id}")
    public ResponseDto getRecruitDetail(@AuthenticationPrincipal PrincipalDetail principalDetail, @PathVariable Long id){
        RecruitBoard board = recruitRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if(principalDetail == null){
            return new ResponseDto<>(HttpStatus.OK.value(), new BoardResponse.NotDetailRecruitResponseDto(board));
        }
        else{
            List<RoleAssignment> roleAssignmentsList = roleAssignmentRepository.findByBoard(board);
            List<BoardResponse.RoleAssignmentDto> roleDto = new ArrayList<>();
            roleAssignmentsList.stream().forEach(r->{
                roleDto.add(convertRoleToDto(r));
            });

            return new ResponseDto<>(HttpStatus.OK.value(), new BoardResponse.DetailRecruitResponseDto(board,roleDto));
        }
    }

    @GetMapping("/api/complete/{id}")
    public ResponseDto<BoardResponse.DetailCompleteResponseDto> getCompleteDetail(@PathVariable Long id){
        CompleteBoard board = completeRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        return new ResponseDto<>(HttpStatus.OK.value(), new BoardResponse.DetailCompleteResponseDto(board));
    }

    @GetMapping("/api/auth/complete/writer")
    public ResponseDto<List<BoardResponse.MemberCompleteBoardListResponseDto>> getMemberComplete(@AuthenticationPrincipal PrincipalDetail principalDetail){
        List<CompleteBoard> boards = boardService.listMemberComplete(principalDetail.getMember());
        List<BoardResponse.MemberCompleteBoardListResponseDto> collect = boards.stream()
                .map(b -> new BoardResponse.MemberCompleteBoardListResponseDto(b))
                .collect(Collectors.toList());

        return new ResponseDto<>(HttpStatus.OK.value(), collect);
    }

    public BoardResponse.RoleAssignmentDto convertRoleToDto(RoleAssignment roleAssignment){
        return new BoardResponse.RoleAssignmentDto(
                roleAssignment.getRole(),
                roleAssignment.getAppliedNumber(),
                roleAssignment.getRequiredNumber()
        );
    }
}
