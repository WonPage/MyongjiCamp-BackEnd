package com.won.myongjiCamp.service;

import com.won.myongjiCamp.dto.request.ApplicationDto;
import com.won.myongjiCamp.exception.AlreadyProcessException;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.application.Application;
import com.won.myongjiCamp.model.application.ApplicationFinalStatus;
import com.won.myongjiCamp.model.application.ApplicationStatus;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.role.Role;
import com.won.myongjiCamp.model.board.role.RoleAssignment;
import com.won.myongjiCamp.repository.ApplicationRepository;
import com.won.myongjiCamp.repository.BoardRepository;
import com.won.myongjiCamp.repository.RoleAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.won.myongjiCamp.model.application.ApplicationFinalStatus.ACCEPTED;
import static com.won.myongjiCamp.model.application.ApplicationStatus.*;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final BoardRepository boardRepository;
    private final ApplicationRepository applicationRepository;
    private final RoleAssignmentRepository roleAssignmentRepository;

    @Transactional
    public void apply(ApplicationDto request, Long id, Member member) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));
        Application application = Application.builder()
                .applicant(member)
                .board(board)
                .content(request.getContent())
                .role(Role.valueOf(request.getRole()))
                .url(request.getUrl())
                .firstStatus(PENDING)
                .build();
    }

    @Transactional
    public void cancel(ApplicationDto request, Long id, Member member) {
        if (ApplicationStatus.valueOf(request.getFirstStatus()) == ApplicationStatus.ACCEPTED) {
            throw new AlreadyProcessException("이미 승인된 지원입니다.");
        } else if (ApplicationStatus.valueOf(request.getFirstStatus()) == REJECTED) {
            throw new AlreadyProcessException("이미 거절된 지원입니다.");
        } else {
            Board board = boardRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));
            applicationRepository.deleteByApplicantAndBoard(member, board);
        }
    }

    //first 지원 수락 or 거절
    @Transactional
    public void firstResult(ApplicationDto request, Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 지원이 존재하지 않습니다."));
        application.setFirstStatus(ApplicationStatus.valueOf(request.getFirstStatus()));
    }

    //final 지원 수락 or 거절
    @Transactional
    public void finalResult(ApplicationDto request, Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 지원이 존재하지 않습니다."));
        application.setFinalStatus(ApplicationFinalStatus.valueOf(request.getFinalStatus()));
        if(ApplicationFinalStatus.valueOf(request.getFinalStatus()) == ACCEPTED){
            RoleAssignment roleAssignment = roleAssignmentRepository.findByBoardAndRole(application.getBoard(), Role.valueOf(request.getRole()))
                    .orElseThrow(() -> new IllegalArgumentException("해당하는 모집분야가 존재하지 않습니다."));
            // if 이미 해당 지원 분야가 꽉차면 있으면 예외 터지게
            int appliedNumber = roleAssignment.getAppliedNumber();
            int requiredNumber = roleAssignment.getRequiredNumber();
            if(appliedNumber >= requiredNumber) {
                throw new IllegalStateException("해당 분야의 모집이 마감되었습니다.");
            }
            roleAssignment.setAppliedNumber(appliedNumber+1);
        }
    }
}
