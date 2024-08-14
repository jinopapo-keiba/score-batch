package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Race {
    String raceName;
    Integer raceLength;
    Integer id;
    Integer round;
    String stadium;
    String oldLimit;
    List<RaceHorse> raceHorses;
    List<Payout> payouts;
}
