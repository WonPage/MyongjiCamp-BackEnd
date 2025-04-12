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
        if (findApplication != null) {
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
        if (application.getFirstStatus() != ApplicationStatus.PENDING) {
            throw new AlreadyProcessException("이미 처리된 지원입니다. 지원 결과를 확인하세요.");
        } else {
            applicationRepository.delete(application);
        }
    }

    @Transactional
    public void processFirstResult(ApplicationRequest request, Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 지원이 존재하지 않습니다."));
        application.setFirstStatus(valueOf(request.getFirstStatus()));
        application.setResultContent(request.getResultContent());
        application.setResultUrl(request.getResultUrl());
        if (application.getFirstStatus() == ACCEPTED) {
            application.setFinalStatus(PENDING);
        }
    }

    @Transactional
    public void processFinalResult(ApplicationRequest request, Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 지원이 존재하지 않습니다."));

        Long boardId = application.getBoard().getId();
        RecruitBoard board = recruitRepository.findById(boardId)
                .orElseThrow((() -> new IllegalArgumentException("해당하는 글이 존재하지 않습니다.")));

        RoleAssignment roleAssignment = roleAssignmentRepository.findByBoardAndRole(board, application.getRole())
                .orElseThrow(() -> new IllegalArgumentException("해당하는 모집분야가 존재하지 않습니다."));

        checkIsFullAndGetAppliedNumber(roleAssignment);

        ApplicationFinalStatus applicationFinalStatus = ApplicationFinalStatus.valueOf(request.getFinalStatus());
        application.setFinalStatus(applicationFinalStatus);
        acceptanceProcess(applicationFinalStatus, roleAssignment, board);
    }

    private void acceptanceProcess(ApplicationFinalStatus applicationFinalStatus, RoleAssignment roleAssignment,
                                   RecruitBoard board) {
        if (applicationFinalStatus.equals(ApplicationFinalStatus.ACCEPTED)) {
            roleAssignment.addAppliedNumber();

            if (roleAssignment.getRequiredNumber() == roleAssignment.getAppliedNumber()) {
                roleAssignment.setFull(true);
            }
            updateBoardStatusIfRecruitmentComplete(board);
        }
    }

    private void updateBoardStatusIfRecruitmentComplete(RecruitBoard board) {
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

    private void checkIsFullAndGetAppliedNumber(RoleAssignment roleAssignment) {
        int appliedNumber = roleAssignment.getAppliedNumber();
        int requiredNumber = roleAssignment.getRequiredNumber();
        if (appliedNumber >= requiredNumber) {
            throw new IllegalStateException("해당 분야의 모집이 마감되었습니다.");
        }
    }

    public List<RecruitBoard> getRecruiterBoardList(Member writer) {
        List<RecruitBoard> boards = recruitRepository.findByMember(writer);
        return boards;
    }

    public List<Application> getListOfApplicant(Member applicant) {
        List<Application> applications = applicationRepository.findByApplicant(applicant);
        return applications;
    }

    public Application getDetailApplication(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 지원서가 존재하지 않습니다."));
        return application;
    }

    public List<Application> getListOfApplicationsForBoard(Long boardId, Member member) {
        RecruitBoard recruitBoard = recruitRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 글이 존재하지 않습니다."));

        if (!recruitBoard.getMember().getId().equals(member.getId())) {
            throw new IllegalStateException("해당 글에 접근 권한이 없는 사용자입니다.");
        }

        List<Application> applications = applicationRepository.findByBoard(recruitBoard);
        return applications;
    }

    public Application getResultMessage(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 지원이 존재하지 않습니다."));
        return application;
    }
}

