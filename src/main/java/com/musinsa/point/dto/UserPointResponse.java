package com.musinsa.point.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.musinsa.point.entity.UserPointEntity;
import com.musinsa.point.model.PointStatus;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class UserPointResponse {

    private Long id;
    private PointStatus status;
    private int amount;
    private String requestedBy;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    public UserPointResponse(UserPointEntity point) {
        this.id = point.getId();
        this.status = (point.isExpired())? PointStatus.EXPIRED : point.getStatus();
        this.amount = point.getAmount();
        this.requestedBy = point.getRequestedBy();
        this.expireDate = point.getExpireDate();
    }
}
