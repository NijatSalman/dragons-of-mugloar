package com.company.dragons_of_mugloar.game.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameState {

    private int lives;
    private int gold;
    private int score;
    private int actionCount;
    private String gameId;

    public void incrementActions() {
        this.actionCount++;
    }

    public void resetActionCount() {
        this.actionCount = 0;
    }

}
