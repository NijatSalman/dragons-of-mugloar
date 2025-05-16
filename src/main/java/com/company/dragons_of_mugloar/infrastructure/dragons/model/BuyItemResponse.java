package com.company.dragons_of_mugloar.infrastructure.dragons.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuyItemResponse {

    private boolean shoppingSuccess;
    private int gold;
    private int lives;
    private int level;
    private int turn;

}
