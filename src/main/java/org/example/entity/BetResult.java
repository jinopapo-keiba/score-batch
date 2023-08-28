package org.example.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BetResult {
    boolean popularFirst;
    boolean popularSecond;
    boolean popularThird;
    boolean jikuFirst;
    boolean jikuSecond;
    boolean jikuThird;
    boolean first;
    boolean second;
    boolean third;
}
