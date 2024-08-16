package org.example.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Payout {
    String frameNumber;
    Float payout;
    String betType;
}
