package com.company.dragons_of_mugloar.game.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DragonGameResponse {

    private String gameId;
    private int score;
    private String message;

}
