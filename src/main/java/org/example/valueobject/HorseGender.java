package org.example.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum HorseGender {
    LADY("牝",0),
    MAN("牡",1),
    CASTRATION("せん",2),
    NONE("",-1);
    private String text;
    private int value;

    public static HorseGender toEnum(int value){
        return Arrays.stream(values())
                .filter(v -> v.getValue() == value)
                .findFirst()
                .orElse(NONE);
    }

    public static HorseGender toEnum(String text){
        return Arrays.stream(values())
                .filter(v -> v.getText().equals(text))
                .findFirst()
                .orElse(NONE);
    }
}
