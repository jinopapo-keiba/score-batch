package org.example.entity;

import lombok.Data;

@Data
public class HorseScore {
    int horseId;
    String horseName;
    int score;
    double rate;
    int maxScore;
}
