package com.ticketing.review.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;


@FeignClient("order-service")
public interface OrderClient {

}
