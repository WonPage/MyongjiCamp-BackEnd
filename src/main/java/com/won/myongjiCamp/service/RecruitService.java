package com.won.myongjiCamp.service;

import com.won.myongjiCamp.dto.RecruitDto;
import com.won.myongjiCamp.dto.RoleAssignmentDto;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.application.Application;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.RecruitBoard;
import com.won.myongjiCamp.model.board.RecruitStatus;
import com.won.myongjiCamp.model.board.role.Role;
import com.won.myongjiCamp.model.board.role.RoleAssignment;
import com.won.myongjiCamp.repository.ApplicationRepository;
import com.won.myongjiCamp.repository.RecruitRepository;
import com.won.myongjiCamp.repository.RoleAssignmentRepository;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.stylesheets.LinkStyle;

//import java.sql.Timestamp;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecruitService {
    private final RecruitRepository recruitRepository;
    private final RoleAssignmentRepository roleAssignmentRepository;
    private final ApplicationRepository applicationRepository;

    // 게시글 작성
    @Transactional
    public void create(RecruitDto recruitDto, Member member){

        RecruitBoard recruitBoard = new RecruitBoard();
        recruitBoard.setTitle(recruitDto.getTitle()); // 제목
        recruitBoard.setContent(recruitDto.getContent()); // 내용
        recruitBoard.setStatus(RecruitStatus.RECRUIT_ONGOING); // 진행 중 -> 넣어주기
        recruitBoard.setPreferredLocation(recruitDto.getPreferredLocation()); //모집 장소
        recruitBoard.setExpectedDuration(recruitDto.getExpectedDuration()); //예상 기간
        recruitBoard.setMember(member);

        recruitRepository.save(recruitBoard);

        for(RoleAssignmentDto roleAssignmentDto : recruitDto.getRoleAssignments()){

            // 구한 사람 수 == 구하는 사람 수 일 경우 RoleAssignment의 isFull값을 변경하기 위해 if문 사용
            if(roleAssignmentDto.getAppliedNumber().equals(roleAssignmentDto.getRequiredNumber())) {
                RoleAssignment roleAssignment = RoleAssignment.builder()
                        .board(recruitBoard)
                        .role(Role.valueOf(roleAssignmentDto.getRole()))
                        .requiredNumber(roleAssignmentDto.getRequiredNumber())
                        .appliedNumber(roleAssignmentDto.getAppliedNumber())
                        .isFull(true)
                        .build();
                roleAssignmentRepository.save(roleAssignment);
            }
            else{
                RoleAssignment roleAssignment = RoleAssignment.builder()
                        .board(recruitBoard)
                        .role(Role.valueOf(roleAssignmentDto.getRole()))
                        .requiredNumber(roleAssignmentDto.getRequiredNumber())
                        .appliedNumber(roleAssignmentDto.getAppliedNumber())
                        .isFull(false)
                        .build();
                roleAssignmentRepository.save(roleAssignment);
            }

        }

    }


    // 게시글 수정
    @Transactional
    public void update(RecruitDto recruitDto, Long id){

        Boolean allFull = true;
        RecruitBoard recruitBoard = recruitRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        recruitBoard.setTitle(recruitDto.getTitle());
        recruitBoard.setContent(recruitDto.getContent());
        recruitBoard.setPreferredLocation(recruitDto.getPreferredLocation());
        recruitBoard.setExpectedDuration(recruitDto.getExpectedDuration());
        recruitBoard.setModifiedDate(new Timestamp(System.currentTimeMillis()));

        for(RoleAssignmentDto roleAssignmentDto : recruitDto.getRoleAssignments()) {
            RoleAssignment roleAssignment = roleAssignmentRepository.findByBoardAndRole(recruitBoard, Role.valueOf(roleAssignmentDto.getRole())).orElse(null);
            if(roleAssignment != null && roleAssignmentDto.getRequiredNumber()==0 && roleAssignmentDto.getAppliedNumber()==0) {
                roleAssignmentRepository.deleteByBoardAndRole(recruitBoard, Role.valueOf(roleAssignmentDto.getRole()));
            }
            else if(roleAssignment != null){
                roleAssignment.setAppliedNumber(roleAssignmentDto.getAppliedNumber());
                roleAssignment.setRequiredNumber(roleAssignmentDto.getRequiredNumber());

                // 구한 사람 수 == 구하는 사람 수 일 경우, RoleAssignment의 isFull값을 변경하기 위해 if문 사용
                if(roleAssignmentDto.getRequiredNumber().equals(roleAssignmentDto.getAppliedNumber())){
                   roleAssignment.setFull(true);
                }
               else{ // 역할들 중 하나라도 구한 사람 수 != 구하는 사람 수 일 경우, RecruitStatus.RECRUIT_ONGOING이도록
                   roleAssignment.setFull(false);
                   allFull = false; //모집 가능 인원이 다 차지 않은 구간이 있다.-> 상태 RECRUIT_ONGOING
               }
            }
            else{
                if(roleAssignmentDto.getAppliedNumber().equals(roleAssignmentDto.getRequiredNumber())) {
                    roleAssignment = RoleAssignment.builder()
                            .board(recruitBoard)
                            .role(Role.valueOf(roleAssignmentDto.getRole()))
                            .requiredNumber(roleAssignmentDto.getRequiredNumber())
                            .appliedNumber(roleAssignmentDto.getAppliedNumber())
                            .isFull(true)
                            .build();
                    roleAssignmentRepository.save(roleAssignment);
                }
                else{
                    roleAssignment = RoleAssignment.builder()
                            .board(recruitBoard)
                            .role(Role.valueOf(roleAssignmentDto.getRole()))
                            .requiredNumber(roleAssignmentDto.getRequiredNumber())
                            .appliedNumber(roleAssignmentDto.getAppliedNumber())
                            .isFull(false)
                            .build();
                    roleAssignmentRepository.save(roleAssignment);

                    allFull = false;

                }
            }
        }

        // 모든 역할들이 구한 사람 수 == 구하는 사람 수이다.
        if(allFull){
            recruitBoard.setStatus(RecruitStatus.RECRUIT_COMPLETE);
        }
        else{
            recruitBoard.setStatus(RecruitStatus.RECRUIT_ONGOING);
        }


    }
    public RecruitBoard recruitDetail(Long id) {
        RecruitBoard recruitBoard = recruitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        // Board를 참조하는 Application들을 찾음
        List<Application> applications = applicationRepository.findByBoard(recruitBoard);

        // 참조하는 Application들의 board 필드를 null로 설정
        for (Application application : applications) {
            application.setBoard(null);
        }

        return recruitBoard;

    }

    // 게시글 삭제
    @Transactional
    public void delete(Long id){
        RecruitBoard recruitBoard = recruitRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        recruitRepository.delete(recruitBoard);
    }

    //게시글 상세 조회
    public RecruitBoard recruitDetail(long id) {
        RecruitBoard recruitBoard = recruitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
     /*   1. 제목과 내용은 로그인 필요 x
        2. 역할과 댓글은 로그인 필요*/
        return recruitBoard;

    }




}
