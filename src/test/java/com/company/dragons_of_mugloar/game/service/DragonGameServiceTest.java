package com.company.dragons_of_mugloar.game.service;

import com.company.dragons_of_mugloar.game.model.DragonGameResponse;
import com.company.dragons_of_mugloar.game.model.GameState;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.GameStartResponse;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.ShopItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DragonGameServiceTest {

    private GameService gameService;
    private ShopService shopService;
    private GamePlayerService gamePlayerService;
    private DragonGameService dragonGameService;

    @BeforeEach
    void setUp() {
        gameService = mock(GameService.class);
        shopService = mock(ShopService.class);
        gamePlayerService = mock(GamePlayerService.class);
        dragonGameService = new DragonGameService(gameService, shopService, gamePlayerService);
    }

    @Test
    void startGameShouldInitializeStateAndDelegateToGamePlayer() {
        GameStartResponse startResponse = createGameStartResponse("game-001", 3, 150, 1, 10, 100, 1);
        List<ShopItem> shopItems = List.of(
                createShopItem("tricks", "Book of Tricks", 100),
                createShopItem("gas", "Gasoline", 100)
        );
        DragonGameResponse expectedResponse = createDragonGameResponse("game-001", 10, "Started successfully");


        when(gameService.startGame()).thenReturn(startResponse);
        when(shopService.getShopItems("game-001")).thenReturn(shopItems);
        when(gamePlayerService.play(eq(shopItems), any(GameState.class))).thenReturn(expectedResponse);

        DragonGameResponse result = dragonGameService.startGame();


        assertEquals("game-001", result.getGameId());
        assertEquals(10, result.getScore());
        assertEquals("Started successfully", result.getMessage());

        verify(gameService).startGame();
        verify(shopService).getShopItems("game-001");
        verify(gamePlayerService).play(eq(shopItems), argThat(state ->
                state.getGameId().equals("game-001") &&
                        state.getLives() == 3 &&
                        state.getGold() == 150 &&
                        state.getScore() == 10 &&
                        state.getActionCount() == 0
        ));
    }

    private GameStartResponse createGameStartResponse(String gameId, int lives, int gold, int level, int score, int highScore, int turn) {
        return GameStartResponse.builder()
                .gameId(gameId)
                .lives(lives)
                .gold(gold)
                .level(level)
                .score(score)
                .highScore(highScore)
                .turn(turn)
                .build();
    }

    private ShopItem createShopItem(String id, String name, int cost) {
        return ShopItem.builder()
                .id(id)
                .name(name)
                .cost(cost)
                .build();
    }

    private DragonGameResponse createDragonGameResponse(String gameId, int score, String message) {
        return DragonGameResponse.builder()
                .gameId(gameId)
                .score(score)
                .message(message)
                .build();
    }
}
