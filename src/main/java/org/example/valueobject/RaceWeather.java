package org.example.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum RaceWeather {
    SUNNY("晴",0),
    CLOUDY("曇",1),
    RAIN("雨",2),
    LIGHT_RAIN("小雨",3);
    private String text;
    private int value;

    public static RaceWeather toEnum(int value){
        return Arrays.stream(values())
                .filter(v -> v.getValue() == value)
                .findFirst()
                .orElse(null);
    }

    public static RaceWeather toEnum(String text){
        return Arrays.stream(values())
                .filter(v -> v.getText().equals(text))
                .findFirst()
                .orElse(null);
    }
}
