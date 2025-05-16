package com.company.dragons_of_mugloar.infrastructure.dragons.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameSolveResponse {

    private Boolean success;
    private int lives;
    private int gold;
    private int score;
    private int highScore;
    private int turn;
    private String message;

}
