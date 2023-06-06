package org.example.entity;

import org.example.valueobject.HorseGender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Horse {
    Integer id;
    String name;
}
