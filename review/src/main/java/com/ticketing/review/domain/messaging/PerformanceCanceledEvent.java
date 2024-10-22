package com.ticketing.review.domain.messaging;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceCanceledEvent {

  private UUID performanceId;
  private Long userId;
}
