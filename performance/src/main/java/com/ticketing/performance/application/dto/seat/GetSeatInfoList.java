package com.ticketing.performance.application.dto.seat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class GetSeatInfoList implements Serializable {

    private List<GetSeatInfoDto> list;

    public static GetSeatInfoList of(List<GetSeatInfoDto> list) {
        return GetSeatInfoList.builder()
                .list(list)
                .build();
    }

}

