package com.musinsa.point.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.Min;

@Setter
@Getter
@NoArgsConstructor
public class PageInfo {

    @Min(value = 1, message = "페이지는 0보다 커야 합니다.")
    private int page = 1;

    @Range(min = 1, max = 20, message = "페이지 사이즈는 1~20 사이여야 합니다.")
    private int size = 20;

    public Pageable toPageable() {
        return PageRequest.of(page - 1, size);
    }
}
