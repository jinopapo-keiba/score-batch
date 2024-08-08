package org.example.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BetResult {
    boolean hit;
    float payout;
}
