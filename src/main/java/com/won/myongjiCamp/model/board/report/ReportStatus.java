package com.won.myongjiCamp.model.board.report;

public enum ReportStatus {
    Reported //신고
    ,WARNING //신고 5번 이상
    , UnderReview //검토중 -> 검토 중일 경우 사용 (신고를 인지했지만 처분이 늦어질 경우)
    , COMPLETED //처리완료
    , NoActionTaken //조치없음
}