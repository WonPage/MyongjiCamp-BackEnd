package com.won.myongjiCamp.dto;

import com.won.myongjiCamp.model.board.report.ReportReason;
import com.won.myongjiCamp.model.board.report.ReportStatus;
import com.won.myongjiCamp.model.board.report.ReportTargetType;
import lombok.Data;

@Data
public class ReportDto {

//    private Long id;

    private ReportTargetType targetType;

    private Long targetId;

    private Long reporterId;

    private ReportReason reason;

    private ReportStatus status;
}
