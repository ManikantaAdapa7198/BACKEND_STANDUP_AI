package com.innocito.standupai.dto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class StandUpResponseDTO {
    private String fullName;
    private String email;
    private String yesterday;
    private String today;
    private String blockers;
    private LocalDate date;
    private String teamName;
    private String todayMood;
}
