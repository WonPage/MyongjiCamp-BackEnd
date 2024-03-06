package com.won.myongjiCamp.service;

import com.won.myongjiCamp.dto.ReportDto;
import com.won.myongjiCamp.model.Member;
import com.won.myongjiCamp.model.board.Board;
import com.won.myongjiCamp.model.board.Comment;
import com.won.myongjiCamp.model.board.report.Report;
import com.won.myongjiCamp.model.board.report.ReportStatus;
import com.won.myongjiCamp.model.board.report.ReportTargetType;
import com.won.myongjiCamp.repository.BoardRepository;
import com.won.myongjiCamp.repository.CommentRepository;
import com.won.myongjiCamp.repository.MemberRepository;
import com.won.myongjiCamp.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;


    // 신고 생성
   @Transactional
    public void createReport(ReportDto reportDto, Member reporter,Long id){ //member는 reporter

       if(reportDto.getTargetType().equals(ReportTargetType.Post) ){ // 게시글 신고의 경우
           Board targetBoard = boardRepository.findById(id)
                   .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
           Report existingReport = reportRepository.findByReporterAndReportedBoard(reporter, targetBoard);
           if(existingReport == null){ // 이전에 신고한 적 없는 경우
               Report report = new Report();
               report.setTargetType(ReportTargetType.Post);
               report.setReportedBoard(targetBoard);
               report.setReporter(reporter);
               report.setReason(reportDto.getReason());
               report.setStatus(ReportStatus.Reported);
               targetBoard.setReportCount(targetBoard.getReportCount()+1);

               reportRepository.save(report);
           }
           else{       // 이전에 신고한 사람의 경우
               existingReport.setReason(reportDto.getReason()); // 신고 사유만 갱신
//               throw new IllegalStateException("이미 신고된 댓글입니다."); // 한 번 신고하면 다시는 신고 못하게
           }
       }
       else{ // 댓글 신고의 경우
           Comment targetComment = commentRepository.findById(id)
                   .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
           Report existingComment = reportRepository.findByReporterAndReportedComment(reporter, targetComment);
           if(existingComment == null){
               Report report = new Report();
               report.setTargetType(ReportTargetType.Comment);
               report.setReportedComment(targetComment);
               report.setReporter(reporter);
               report.setReason(reportDto.getReason());
               report.setStatus(ReportStatus.Reported);
               targetComment.setReportCount(targetComment.getReportCount()+1);

               reportRepository.save(report);
           }
           else{
               existingComment.setReason(reportDto.getReason()); // 신고 사유만 갱신
//               throw new IllegalStateException("이미 신고된 댓글입니다.");
           }
       }
   }

   // 신고가 5번 이상 들어가면 신고 상태 바뀔꺼야
    // 신고 상태가 underReview인 애들 조회
//5번째 신고한테만????

    // 해당 글에 신고를 전부 다 상태 전환 and 해당 글 삭제.


}
