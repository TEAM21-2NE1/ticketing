package com.ticketing.order.domain.model;

public enum OrderStatus {
    PENDING_PAYMENT,   // 결제 대기 중
    RESERVED,          // 예약 완료
    CANCELED,          // 예약 취소
    REFUNDED           // 환불 처리
}
