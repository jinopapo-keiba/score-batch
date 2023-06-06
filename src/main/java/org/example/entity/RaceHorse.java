package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaceHorse {
    Integer weight;
    Integer old;
    Integer frameNumber;
    Horse horse;
    Jockey jockey;
    RaceResult raceResult;
}
