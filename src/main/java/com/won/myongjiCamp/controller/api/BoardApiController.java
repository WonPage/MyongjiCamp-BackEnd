package com.won.myongjiCamp.controller.api;

import com.won.myongjiCamp.config.auth.PrincipalDetail;
import com.won.myongjiCamp.dto.RecruitDto;
import com.won.myongjiCamp.dto.ResponseDto;
import com.won.myongjiCamp.dto.RoleAssignmentDto;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.dto.request.BoardSearchDto;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.RecruitBoard;
import com.won.myongjiCamp.model.board.RecruitStatus;
import com.won.myongjiCamp.model.board.role.Role;
import com.won.myongjiCamp.model.board.role.RoleAssignment;
import com.won.myongjiCamp.repository.BoardRepository;
import com.won.myongjiCamp.repository.MemberRepository;
import com.won.myongjiCamp.repository.RecruitRepository;
import com.won.myongjiCamp.repository.RoleAssignmentRepository;
import com.won.myongjiCamp.service.BoardService;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BoardApiController {

    private final RecruitService recruitService;

    private final MemberRepository memberRepository;

    private final BoardService boardService;

    private final RoleAssignmentRepository roleAssignmentRepository;

    private final BoardRepository boardRepository;
    private final RecruitRepository recruitRepository;
    // 게시글 작성
/*    @PostMapping("/api/auth/recruit")
    public ResponseDto<String> createRecruit(@RequestBody @Valid RecruitDto recruitDto,@AuthenticationPrincipal PrincipalDetail principal){
        recruitService.create(recruitDto,principal.getMember());
        return new ResponseDto<String>(HttpStatus.OK.value(),"게시글 작성 완료");
    }*/


    // recruit 게시글 작성 테스트용
    @PostMapping("/api/auth/recruit")
    public ResponseDto<String> createRecruit(@RequestBody @Valid RecruitDto recruitDto){
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));
        recruitService.create(recruitDto,member);
        return new ResponseDto<String>(HttpStatus.OK.value(),"게시글 작성 완료");
    }

    // recruit 게시글 수정, id는 게시글 id
    @PutMapping("/api/auth/recruit/{id}")
    public ResponseDto<String> updateRecruit(@RequestBody @Valid RecruitDto recruitDto, @PathVariable long id){
        recruitService.update(recruitDto,id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "게시글이 수정되었습니다.");
    }

//    // complete 게시글 작성
//    @PostMapping("/api/auth/recruit")
//    public ResponseDto<String> createComplete(@RequestBody @Valid RecruitDto recruitDto,@AuthenticationPrincipal PrincipalDetail principal){
//        recruitService.create(recruitDto,principal.getMember());
//        return new ResponseDto<String>(HttpStatus.OK.value(),"게시글 작성 완료");
//    }

//    // complete 게시글 수정, id는 게시글 id
//    @PutMapping("/api/auth/recruit/{id}")
//    public ResponseDto<String> updateRecruit(@RequestBody @Valid RecruitDto recruitDto, @PathVariable long id){
//        recruitService.update(recruitDto,id);
//        return new ResponseDto<String>(HttpStatus.OK.value(), "게시글이 수정되었습니다.");
//    }

    // 게시글 삭제, id는 게시글 id
    @DeleteMapping("/api/auth/recruit/{id}")
    public ResponseDto<String> deleteRecruit(@PathVariable long id){
        recruitService.delete(id);
        return new ResponseDto<String>(HttpStatus.OK.value(), "게시글이 삭제되었습니다.");
    }



    //글 조회(검색)
    @GetMapping("/api/board")
    public Result findAll(@ModelAttribute @Valid BoardSearchDto requestDto) {
        log.info("Request param: {}", requestDto);
        Page<Board> boards = boardService.searchBoards(requestDto);
        List<BoardListResponseDto> collect = boards.stream()
                .map(BoardListResponseDto::new)
                .collect(Collectors.toList());
        return new Result(collect);
    }

    //get할 때는 그냥 Dto로 해주는 것보다는 Result에 담아서 주는 것이 좋다.
    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }


    // 게시글 상세 읽기
    @GetMapping("/api/auth/recruit/{id}")
    public Result getRecruitDetail(@AuthenticationPrincipal PrincipalDetail principalDetail, @PathVariable Long id){

        RecruitBoard board = recruitRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if(principalDetail == null){ // 로그인 x -> 내용, 제목, 닉네임, 아이콘 보임
//            return ResponseEntity.ok(new Result(new NotDetailRecruitResponseDto(recruitBoard));
            return new Result(new NotDetailRecruitResponseDto(board));
        }
        else{ // 로그인 o
            List<RoleAssignment> roleAssignmentsList = roleAssignmentRepository.findByBoard(board);
            List<RoleAssignmentDto> roleDto = new ArrayList<>();
            roleAssignmentsList.stream().forEach(r->{
                roleDto.add(convertRoleToDto(r));
            });

            return new Result(new DetailRecruitResponseDto(board,roleDto));
        }

    }

    public RoleAssignmentDto convertRoleToDto(RoleAssignment roleAssignment){
        return new RoleAssignmentDto(
                roleAssignment.getRole(),
                roleAssignment.getAppliedNumber(),
                roleAssignment.getRequiredNumber()
        );
    }
    @Data
    static class DetailRecruitResponseDto {
        private Long writerId; //글 쓴 사람 id
        private String title;
        private String content;
        private Integer scrapCount;
        private RecruitStatus status; //모집 중 or 모집 완료
        private String preferredLocation; //활동 지역
        private String expectedDuration; //예상 기간
        private List<RoleAssignmentDto> roleAssignments; //역할
        private String nickname; // 글 쓴 사람 닉네임
        private Integer profileIcon; // 글 쓴 사람 아이콘
        private Timestamp modifiedDate;//수정한 날짜
        private Timestamp createDate; //만든 날짜

        public DetailRecruitResponseDto(RecruitBoard recruitBoard,List<RoleAssignmentDto> roleAssignmentDtoList){
            this.writerId = recruitBoard.getMember().getId();
            this.title = recruitBoard.getTitle();
            this.content = recruitBoard.getContent();
            this.status = recruitBoard.getStatus();
            this.preferredLocation = recruitBoard.getPreferredLocation();
            this.expectedDuration = recruitBoard.getExpectedDuration();
            this.nickname = recruitBoard.getMember().getNickname();
            this.profileIcon = recruitBoard.getMember().getProfileIcon();
            this.modifiedDate = recruitBoard.getModifiedDate();
            this.createDate = recruitBoard.getCreateDate();
            this.roleAssignments = roleAssignmentDtoList;
            this.scrapCount = recruitBoard.getScrapCount();


        }

    }
    @Data
    @AllArgsConstructor
    static class NotDetailRecruitResponseDto {
        private Long writerId; // 글 쓴 사람 id
        private String title;
        private String content;
        private Integer scrapCount;
        private RecruitStatus status; //모집 중 or 모집 완료
        private String preferredLocation; //활동 지역
        private String expectedDuration; //예상 기간
        private String nickname; // 글 쓴 사람 닉네임
        private Integer profileIcon; // 글 쓴 사람 아이콘
        private Timestamp modifiedDate;//수정한 날짜
        private Timestamp createDate; //만든 날짜
        public NotDetailRecruitResponseDto(RecruitBoard recruitBoard){
            this.writerId = recruitBoard.getMember().getId();
            this.title = recruitBoard.getTitle();
            this.content = recruitBoard.getContent();
            this.status = recruitBoard.getStatus();
            this.preferredLocation = recruitBoard.getPreferredLocation();
            this.expectedDuration = recruitBoard.getExpectedDuration();
            this.nickname = recruitBoard.getMember().getNickname();
            this.profileIcon = recruitBoard.getMember().getProfileIcon();
            this.modifiedDate = recruitBoard.getModifiedDate();
            this.createDate = recruitBoard.getCreateDate();
            this.scrapCount = recruitBoard.getScrapCount();

        }

    }
    @Data
    public class BoardListResponseDto {
        private Long boardId;
        private String title;
        private Timestamp modifiedDate;
        private List<Role> roles;
        private String expectedDuration;
        private int commentCount;
        private int scrapCount;

        public BoardListResponseDto(Board board) {
            this.boardId = board.getId();
            this.title = board.getTitle();
            this.modifiedDate = board.getModifiedDate();
            this.commentCount = board.getCommentCount();
            this.scrapCount = board.getScrapCount();

            if (board instanceof RecruitBoard recruitBoard) {
                this.roles = recruitBoard.getRoles().stream()
                        .map(RoleAssignment::getRole)
                        .collect(Collectors.toList());
                this.expectedDuration = recruitBoard.getExpectedDuration();
            }
        }
    }

}
