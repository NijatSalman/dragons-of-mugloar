package com.company.dragons_of_mugloar.game.service;

import com.company.dragons_of_mugloar.game.model.DragonGameResponse;
import com.company.dragons_of_mugloar.game.model.GameState;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.BuyItemResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStateHelperTest {

    private final GameStateHelper helper = new GameStateHelper();

    @Test
    void isGameOverShouldReturnTrueWhenScoreReachesTarget() {
        GameState state = createGameState(3, 100, "game-over-score", 0, 1000);
        assertTrue(helper.isGameOver(state));
    }

    @Test
    void isGameOverShouldReturnTrueWhenLivesAtDeathThreshold() {
        GameState state = createGameState(0, 200, "game-dead", 0, 500);
        assertTrue(helper.isGameOver(state));
    }

    @Test
    void isGameOverShouldReturnFalseWhenScoreAndLivesAreValid() {
        GameState state = createGameState(3, 150, "game-active", 0, 300);
        assertFalse(helper.isGameOver(state));
    }

    @Test
    void updateStateShouldUpdateGoldLivesAndIncrementActions() {
        GameState state = createGameState(2, 50, "game-update", 1, 300);
        BuyItemResponse response = createBuyItemResponse(3, 80, 1, 5, true);

        helper.updateState(state, response);

        assertEquals(3, state.getLives());
        assertEquals(80, state.getGold());
        assertEquals(2, state.getActionCount());
    }

    @Test
    void buildFinalResponseShouldReturnGameOverMessageWhenLivesZero() {
        GameState state = createGameState(0, 100, "game-end", 3, 600);

        DragonGameResponse result = helper.buildFinalResponse(state);

        assertEquals("game-end", result.getGameId());
        assertEquals(600, result.getScore());
        assertEquals("Game over", result.getMessage());
    }

    @Test
    void buildFinalResponseShouldReturnSuccessMessageWhenGameIsNotOver() {
        GameState state = createGameState(3, 200, "game-success", 4, 900);

        DragonGameResponse result = helper.buildFinalResponse(state);

        assertEquals("game-success", result.getGameId());
        assertEquals(900, result.getScore());
        assertEquals("You win! Game finished successfully", result.getMessage());
    }

    private GameState createGameState(int lives, int gold, String gameId, int actionCount, int score) {
        return GameState.builder()
                .lives(lives)
                .gold(gold)
                .gameId(gameId)
                .actionCount(actionCount)
                .score(score)
                .build();
    }

    private BuyItemResponse createBuyItemResponse(int lives, int gold, int level, int turn, boolean success) {
        return BuyItemResponse.builder()
                .lives(lives)
                .gold(gold)
                .level(level)
                .turn(turn)
                .shoppingSuccess(success)
                .build();
    }
}