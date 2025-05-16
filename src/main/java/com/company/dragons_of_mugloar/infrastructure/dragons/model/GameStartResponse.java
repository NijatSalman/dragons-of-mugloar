package com.company.dragons_of_mugloar.infrastructure.dragons.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameStartResponse {

    private String gameId;
    private int lives;
    private int gold;
    private int level;
    private int score;
    private int highScore;
    private int turn;

}
