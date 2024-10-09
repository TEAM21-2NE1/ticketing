package com.ticketing.order.common.response;

import lombok.NonNull;

public interface CommonResponse {

    @NonNull
    String message();

    int resultCode();

}
