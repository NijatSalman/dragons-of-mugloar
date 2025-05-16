package com.company.dragons_of_mugloar.game.service;

import com.company.dragons_of_mugloar.game.model.DragonGameResponse;
import com.company.dragons_of_mugloar.game.model.GameState;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.BuyItemResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.company.dragons_of_mugloar.game.model.GameConstants.DEATH_THRESHOLD;
import static com.company.dragons_of_mugloar.game.model.GameConstants.TARGET_SCORE;

@Slf4j
@Service
public class GameStateHelper {

    protected boolean isGameOver(GameState state) {
        return state.getScore() >= TARGET_SCORE || state.getLives() <= DEATH_THRESHOLD;
    }

    protected void updateState(GameState state, BuyItemResponse result) {
        log.debug("Updating state after purchase. New gold: {}, new lives: {}", result.getGold(), result.getLives());
        state.setGold(result.getGold());
        state.setLives(result.getLives());
        state.incrementActions();
    }

    protected DragonGameResponse buildFinalResponse(GameState state) {
        log.info("Building final response for game ID: {}, score: {}", state.getGameId(), state.getScore());
        return DragonGameResponse.builder()
                .gameId(state.getGameId())
                .score(state.getScore())
                .message(state.getLives() <= DEATH_THRESHOLD ? "Game over" : "You win! Game finished successfully")
                .build();
    }

}
