package com.ticketing.order.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class User {
    private String userId;
}
