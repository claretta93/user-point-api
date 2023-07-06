package com.musinsa.point.model.dto;

import javax.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserPointRequest {

    private Long userId;
    private Long requestId;
    private String requestedBy;

    @Min(value = 1, message = "포인트 액수는 0보다 커야 합니다.")
    private int amount;

    public UserPointRequest(Long userId, Long requestId, String requestedBy, int amount) {
        this.userId = userId;
        this.requestId = requestId;
        this.requestedBy = requestedBy;
        this.amount = amount;
    }
}
