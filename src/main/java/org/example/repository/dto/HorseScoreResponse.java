package org.example.repository.dto;

import lombok.Data;
import org.example.entity.Confident;
import org.example.entity.HorseScore;

import java.util.List;

@Data
public class HorseScoreResponse {
    List<HorseScore> scores;
    Confident confident;
}
