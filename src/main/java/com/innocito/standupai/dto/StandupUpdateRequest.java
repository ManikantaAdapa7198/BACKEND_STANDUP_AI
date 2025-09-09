package com.innocito.standupai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class StandupUpdateRequest {
    @NotBlank
    private String yesterday;

    @NotBlank
    private String today;

    @NotBlank
    private String teamName;

    private String todayMood;

    private String blockers;

}