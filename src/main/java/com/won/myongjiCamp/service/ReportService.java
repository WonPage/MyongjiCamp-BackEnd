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
           Report existingReport = reportRepository.findByReporterIdAndReportedBoardId(reporter.getId(), targetBoard.getId());
           if(existingReport == null){ // 이전에 신고한 적 없는 경우
               Report report = new Report();
               report.setTargetType(ReportTargetType.Post);
               report.setReportedBoardId(targetBoard.getId());
               report.setReporterId(reporter.getId());
               report.setReason(reportDto.getReason());
               report.setStatus(ReportStatus.Reported);


//               targetBoard.setReportStatus(ReportStatus.Reported);

               reportRepository.save(report);
           }
           else{       // 이전에 신고한 사람의 경우
               existingReport.setReason(reportDto.getReason()); // 신고 사유만 갱신
               reportRepository.save(existingReport);

//               throw new IllegalStateException("이미 신고된 댓글입니다."); // 한 번 신고하면 다시는 신고 못하게
           }
           if(targetBoard.getReportCount()>0){
               targetBoard.setReportStatus(ReportStatus.Reported);
           }
           else if(targetBoard.getReportCount()>=5){
               targetBoard.setReportStatus(ReportStatus.WARNING);

           }


       }
       else{ // 댓글 신고의 경우
           Comment targetComment = commentRepository.findById(id)
                   .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
           Report existingComment = reportRepository.findByReporterIdAndReportedCommentId(reporter.getId(), targetComment.getId());
           if(existingComment == null){
               Report report = new Report();
               report.setTargetType(ReportTargetType.Comment);
               report.setReportedCommentId(targetComment.getId());
               report.setReporterId(reporter.getId());
               report.setReason(reportDto.getReason());
               report.setStatus(ReportStatus.Reported);
               targetComment.setReportStatus(ReportStatus.Reported);
               //수정 : getReportCount가 기본 0 이면 null로 저장돼서 에러가 난다. if, else문 추가
               if (targetComment.getReportCount() == null){
                   targetComment.setReportCount(1);
               }else{
                   targetComment.setReportCount(targetComment.getReportCount()+1);
               }

               reportRepository.save(report);
           }
           else{
               existingComment.setReason(reportDto.getReason()); // 신고 사유만 갱신
               reportRepository.save(existingComment);

//               throw new IllegalStateException("이미 신고된 댓글입니다.");
           }
           if(targetComment.getReportCount()>0){
               targetComment.setReportStatus(ReportStatus.Reported);
           }
           else if(targetComment.getReportCount()>=5){
               targetComment.setReportStatus(ReportStatus.WARNING);

           }

       }
   }

/*   public void findReported(){ // --> 관리자 권한이 필요함, 아직 관리자 페이지 x
       reportRepository.findReportedBoards(ReportStatus.Reported);
   }

   public void findWarning(){ // --> 관리자 권한이 필요함, 아직 관리자 페이지 x
       reportRepository.findReportedBoards(ReportStatus.WARNING); // 오름 차순으로?
   }*/

   // 신고 처리 로직 아직 x



}