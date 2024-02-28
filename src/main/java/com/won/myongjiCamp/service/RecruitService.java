package com.won.myongjiCamp.service;

import com.won.myongjiCamp.dto.RecruitDto;
import com.won.myongjiCamp.dto.RoleAssignmentDto;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.board.RecruitBoard;
import com.won.myongjiCamp.model.board.RecruitStatus;
import com.won.myongjiCamp.model.board.role.Role;
import com.won.myongjiCamp.model.board.role.RoleAssignment;
import com.won.myongjiCamp.repository.RecruitRepository;
import com.won.myongjiCamp.repository.RoleAssignmentRepository;
import jakarta.persistence.Id;
import jdk.jfr.Timestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import java.sql.Timestamp;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecruitService {
    private final RecruitRepository recruitRepository;
    private final RoleAssignmentRepository roleAssignmentRepository;

    // 게시글 작성
    @Transactional
    public void create(RecruitDto recruitDto, Member member){

        RecruitBoard recruitBoard = new RecruitBoard();
//        RoleAssignment roleAssignment = new RoleAssignment();
        recruitBoard.setTitle(recruitDto.getTitle()); // 제목
        recruitBoard.setContent(recruitDto.getContent()); // 내용
        recruitBoard.setStatus(RecruitStatus.RECRUIT_ONGOING); // 진행 중 -> 넣어주기
        recruitBoard.setPreferredLocation(recruitDto.getPreferredLocation()); //모집 장소
        recruitBoard.setExpectedDuration(recruitDto.getExpectedDuration()); //예상 기간
        recruitBoard.setMember(member);
//        recruitBoard.setRoleAssignments(recruitDto.getRoleAssignments());

        recruitRepository.save(recruitBoard);

        for(RoleAssignmentDto roleAssignmentDto : recruitDto.getRoleAssignments()){
            RoleAssignment roleAssignment = RoleAssignment.builder()
                    .board(recruitBoard)
                    .role(Role.valueOf(roleAssignmentDto.getRole()))
                    .requiredNumber(roleAssignmentDto.getRequiredNumber())
                    .appliedNumber(roleAssignmentDto.getAppliedNumber()).build();
            roleAssignmentRepository.save(roleAssignment);
        }

    }

    @Transactional
    public void update(RecruitDto recruitDto, Long id){
        // 일단 role 빼고 구현 -> 진행중도 아직
//        Optional<RecruitBoard> recruitBoard = recruitRepository.findById(member.getId()); (X)
        RecruitBoard recruitBoard = recruitRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
//        recruitBoard.setModifiedDate(new Timestamp(System.currentTimeMillis())); //수정 날짜 변경
        recruitBoard.setTitle(recruitDto.getTitle());
        recruitBoard.setContent(recruitDto.getContent());
        recruitBoard.setPreferredLocation(recruitDto.getPreferredLocation());
        recruitBoard.setExpectedDuration(recruitDto.getExpectedDuration());
        for(RoleAssignmentDto roleAssignmentDto : recruitDto.getRoleAssignments()) {
            RoleAssignment roleAssignment = roleAssignmentRepository.findByBoardAndRole(recruitBoard, Role.valueOf(roleAssignmentDto.getRole())).orElse(null);
            if(roleAssignment != null && roleAssignmentDto.getRequiredNumber()==0 && roleAssignmentDto.getAppliedNumber()==0) {
                roleAssignmentRepository.deleteByBoardAndRole(recruitBoard, Role.valueOf(roleAssignmentDto.getRole()));
            }
            else if(roleAssignment != null){
                roleAssignment.setAppliedNumber(roleAssignmentDto.getAppliedNumber());
                roleAssignment.setRequiredNumber(roleAssignmentDto.getRequiredNumber());
            }
            else{
                roleAssignment = RoleAssignment.builder()
                        .board(recruitBoard)
                        .role(Role.valueOf(roleAssignmentDto.getRole()))
                        .requiredNumber(roleAssignmentDto.getRequiredNumber())
                        .appliedNumber(roleAssignmentDto.getAppliedNumber()).build();
                roleAssignmentRepository.save(roleAssignment);
            }

        }

        }

    @Transactional
    public void delete(Long id){
        RecruitBoard recruitBoard = recruitRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        recruitRepository.delete(recruitBoard);
    }


}
