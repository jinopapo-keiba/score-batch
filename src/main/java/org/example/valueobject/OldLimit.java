package org.example.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum OldLimit {
    ONLY2("2歳",0),
    ONLY3("3歳",1),
    UPPER3("3歳以上",2),
    UPPER4("4歳以上",3);

    private String text;
    private int value;

    public static OldLimit toEnum(int value){
        return Arrays.stream(values())
                .filter(v -> v.getValue() == value)
                .findFirst()
                .orElse(null);
    }

    public static OldLimit toEnum(String text){
        return Arrays.stream(values())
                .filter(v -> v.getText().equals(text))
                .findFirst()
                .orElse(null);
    }
}
