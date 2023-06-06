package org.example.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@AllArgsConstructor
@Getter
@Slf4j
public enum RaceCondition {
    GOOD("良",0),
    MIDDLE("稍重",1),
    HEAVY("重",2),
    BAD("不良",3);

    private String text;
    private int value;


    public static RaceCondition toEnum(int value){
        return Arrays.stream(values())
                .filter(v -> v.getValue() == value)
                .findFirst()
                .orElse(null);
    }

    public static RaceCondition toEnum(String text){
        return Arrays.stream(values())
                .filter(v -> v.getText().equals(text))
                .findFirst()
                .orElse(null);
    }
}
