package org.example.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.valueobject.Grade;

import java.util.Date;

@Data
@NoArgsConstructor
public class Result {
    Date raceDate;
    Grade grade;
    Integer old;
    Integer weight;
    RaceResult raceResult;
}
