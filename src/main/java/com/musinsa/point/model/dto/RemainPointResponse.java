package com.musinsa.point.model.dto;

import lombok.Getter;

@Getter
public class RemainPointResponse {

    private Long userId;
    private int amount;

    public RemainPointResponse(Long userId, int amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
