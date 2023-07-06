package com.musinsa.point.controller;

import com.musinsa.point.model.dto.DefaultResponse;
import com.musinsa.point.model.dto.PageInfo;
import com.musinsa.point.model.dto.PointHistoryPageResponse;
import com.musinsa.point.model.dto.RemainPointResponse;
import com.musinsa.point.model.dto.UserPointRequest;
import com.musinsa.point.service.UserPointService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/point")
public class UserPointController {

    private final UserPointService userPointService;

    public UserPointController(UserPointService userPointService) {
        this.userPointService = userPointService;
    }

    @GetMapping("/remain")
    public ResponseEntity<RemainPointResponse> getRemainPoint(
        @RequestHeader Long userId
    ) {
        var result = userPointService.getRemainPoint(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    public ResponseEntity<PointHistoryPageResponse> getPointHistory(
        @RequestHeader Long userId,
        PageInfo pageInfo
    ) {
        var result = userPointService.getPointHistory(userId, pageInfo.toPageable());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/add")
    public ResponseEntity<DefaultResponse> addPoint(
        @RequestBody UserPointRequest userPointRequest
    ) {
        userPointService.addPoint(userPointRequest);
        return ResponseEntity.ok(DefaultResponse.success());
    }

    @PostMapping("/use")
    public ResponseEntity<DefaultResponse> usePoint(
        @RequestBody UserPointRequest userPointRequest
    ) {
        userPointService.usePoint(userPointRequest);
        return ResponseEntity.ok(DefaultResponse.success());

    }

    @PutMapping("/cancel/use")
    public ResponseEntity<DefaultResponse> cancelUsePoint(
        @RequestParam Long requestId,
        @RequestParam String requestedBy
    ) {
        userPointService.cancelPointUse(requestId, requestedBy);
        return ResponseEntity.ok(DefaultResponse.success());
    }
}
