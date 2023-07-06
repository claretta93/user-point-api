package com.musinsa.point.model.dto;

import com.musinsa.point.model.entity.UserPointEntity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PointHistoryPageResponse {

    private Long userId;
    private Meta meta;
    private List<UserPointResponse> userPointList;

    public PointHistoryPageResponse(Long userId, Page<UserPointEntity> userPointEntityPage) {
        this.userId = userId;
        this.meta = new Meta(userPointEntityPage.getTotalElements(), userPointEntityPage.getTotalPages(), userPointEntityPage.getNumber() + 1,
                userPointEntityPage.getSize(), userPointEntityPage.isLast());
        this.userPointList = userPointEntityPage.stream()
                .map(userPointEntity -> new UserPointResponse(userPointEntity))
                .collect(Collectors.toList());
    }

    @Getter
    public static class Meta {

        private Long totalCount;
        private Integer totalPage;
        private Integer page;
        private Integer pageSize;
        private boolean last;

        public Meta(Long totalCount, Integer totalPage, Integer page, Integer pageSize, Boolean last) {
            this.totalCount = totalCount;
            this.totalPage = totalPage;
            this.page = page;
            this.pageSize = pageSize;
            this.last = last;
        }
    }
}
