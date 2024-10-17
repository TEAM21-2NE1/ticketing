package com.ticketing.review.infrastructure.client.dto;


import com.ticketing.review.application.client.dto.OrderStatusDto;


public record GetOrderStatusResponse(
    String orderStatus
) {

  public OrderStatusDto toOrderStatusDto() {
    return new OrderStatusDto(this.orderStatus());
  }
}