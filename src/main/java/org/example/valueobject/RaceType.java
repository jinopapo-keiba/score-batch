package org.example.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum RaceType {
    TURF("芝",0),
    DIRT("ダート",1);

    private String text;
    private int value;


    public static RaceType toEnum(int value){
        return Arrays.stream(values())
                .filter(v -> v.getValue() == value)
                .findFirst()
                .orElse(null);
    }

    public static RaceType toEnum(String text){
        return Arrays.stream(values())
                .filter(v -> v.getText().equals(text))
                .findFirst()
                .orElse(null);
    }
}
