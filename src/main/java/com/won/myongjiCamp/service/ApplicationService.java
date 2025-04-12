package com.won.myongjiCamp.service;

import com.won.myongjiCamp.dto.request.ApplicationRequest;
import com.won.myongjiCamp.exception.AlreadyProcessException;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.application.Application;
import com.won.myongjiCamp.model.application.ApplicationFinalStatus;
import com.won.myongjiCamp.model.application.ApplicationStatus;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.RecruitBoard;
import com.won.myongjiCamp.model.board.RecruitStatus;
import com.won.myongjiCamp.model.board.role.Role;
import com.won.myongjiCamp.model.board.role.RoleAssignment;
import com.won.myongjiCamp.repository.ApplicationRepository;
import com.won.myongjiCamp.repository.BoardRepository;
import com.won.myongjiCamp.repository.RecruitRepository;
import com.won.myongjiCamp.repository.RoleAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.won.myongjiCamp.model.application.ApplicationFinalStatus.PENDING;
import static com.won.myongjiCamp.model.application.ApplicationStatus.*;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final BoardRepository boardRepository;
    private final ApplicationRepository applicationRepository;
    private final RoleAssignmentRepository roleAssignmentRepository;
    private final RecruitRepository recruitRepository;

    @Transactional
    public void apply(ApplicationRequest request, Long id, Member member) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));
        Application findApplication = applicationRepository.findByApplicantAndBoard(member, board).orElse(null);
        if(findApplication != null) {
            throw new IllegalStateException("이미 지원한 글입니다.");
        }
        Application application = Application.builder()
                .applicant(member)
                .board(board)
                .content(request.getContent())
                .role(Role.valueOf(request.getRole()))
                .url(request.getUrl())
                .firstStatus(ApplicationStatus.PENDING)
                .build();
        applicationRepository.save(application);
    }

    @Transactional
    public void cancel(Long id, Member member) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));
        Application application = applicationRepository.findByApplicantAndBoard(member, board).
                orElseThrow(() -> new IllegalArgumentException("해당 지원이 존재하지 않습니다."));
        if (application.getFirstStatus() != ApplicationStatus.PENDING){
            throw new AlreadyProcessException("이미 처리된 지원입니다. 지원 결과를 확인하세요.");
        } else {
            applicationRepository.delete(application);
        }
    }

    //first 지원 수락 or 거절
    @Transactional
    public void firstResult(ApplicationRequest request, Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 지원이 존재하지 않습니다."));
        application.setFirstStatus(valueOf(request.getFirstStatus()));
        application.setResultContent(request.getResultContent());
        application.setResultUrl(request.getResultUrl());
        if (application.getFirstStatus() == ACCEPTED) {
            application.setFinalStatus(PENDING);
        }
    }

    //final 지원 수락 or 거절
    @Transactional
    public void finalResult(ApplicationRequest request, Long id) {
        //application리포지토리에서 해당 지원 찾기
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 지원이 존재하지 않습니다."));
        //board찾아오기
        Long boardId = application.getBoard().getId();
        RecruitBoard board = recruitRepository.findById(boardId).orElseThrow((() -> new IllegalArgumentException("해당하는 글이 존재하지 않습니다.")));
        //roleAssignment에서 지원자가 지원하는 분야와 해당 글에 해당하는 roleAssignment 객체 가져오기
        RoleAssignment roleAssignment = roleAssignmentRepository.findByBoardAndRole(board, application.getRole())
                .orElseThrow(() -> new IllegalArgumentException("해당하는 모집분야가 존재하지 않습니다."));

        // if 이미 해당 지원 분야가 꽉차면 있으면 예외 터지게
        int appliedNumber = roleAssignment.getAppliedNumber();
        int requiredNumber = roleAssignment.getRequiredNumber();
        if (appliedNumber >= requiredNumber) {
            throw new IllegalStateException("해당 분야의 모집이 마감되었습니다.");
        }
        //최종 수락인지 거절인지 dto로 받아와서 담기
        ApplicationFinalStatus applicationFinalStatus = ApplicationFinalStatus.valueOf(request.getFinalStatus());
        application.setFinalStatus(applicationFinalStatus);
        if(applicationFinalStatus.equals(ApplicationFinalStatus.ACCEPTED)){
            roleAssignment.setAppliedNumber(appliedNumber + 1);
            //해당 role이 다 찬다면
            if(roleAssignment.getRequiredNumber() == roleAssignment.getAppliedNumber()){
                roleAssignment.setFull(true);
            }
            // 지금 수락한다면 ? -> 모든 모집 인원이 차면? -> 모집완료 상태로 변환
            List<RoleAssignment> role = roleAssignmentRepository.findByBoard(board);
            AtomicBoolean state = new AtomicBoolean(true);
            role.stream().forEach(r -> {
                if (r.getAppliedNumber() != r.getRequiredNumber()) {
                    state.set(false);
                }
            });
            if (state.get()) {
                board.setStatus(RecruitStatus.RECRUIT_COMPLETE);
            }
        }
    }

    public List<RecruitBoard> listOfWriter(Member writer) {
        List<RecruitBoard> boards = recruitRepository.findByMember(writer);
        return boards;
    }

    public List<Application> listOfApplicant(Member applicant) {
        List<Application> applications = applicationRepository.findByApplicant(applicant);
        return applications;
    }

    public Application detailApplication(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 지원서가 존재하지 않습니다."));
        return application;
    }

    public List<Application> listApplication(Long boardId, Member member) {
        RecruitBoard recruitBoard = recruitRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 글이 존재하지 않습니다."));

        if (!recruitBoard.getMember().getId().equals(member.getId())) {
            throw new IllegalStateException("해당 글에 접근 권한이 없는 사용자입니다.");
        }

        List<Application> applications = applicationRepository.findByBoard(recruitBoard);
        return applications;
    }

    public Application resultMessage(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 지원이 존재하지 않습니다."));
        return application;

    }
}

