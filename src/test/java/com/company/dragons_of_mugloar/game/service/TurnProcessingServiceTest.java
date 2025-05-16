package com.company.dragons_of_mugloar.game.service;

import com.company.dragons_of_mugloar.game.model.GameState;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.BuyItemResponse;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.GameSolveResponse;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.Message;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.ShopItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class TurnProcessingServiceTest {

    private MessageService messageService;
    private ShopService shopService;
    private GameStateHelper gameStateHelper;
    private TurnProcessingService service;

    @BeforeEach
    void setUp() {
        messageService = mock(MessageService.class);
        shopService = mock(ShopService.class);
        gameStateHelper = mock(GameStateHelper.class);
        service = new TurnProcessingService(messageService, shopService, gameStateHelper);
    }

    @Test
    void processTurnShouldSkipWhenTaskExpired() {
        GameState state = createGameState(3, 100, "game-expired", 4);
        Message task = createMessage("YYMjMSTH", "Steal sheep delivery", 44, 3, "Walk in the park");

        service.processTurn(state, List.of(), task);

        verify(messageService, never()).solveTask(any(), any());
    }

    @Test
    void processTurnShouldSkipWhenGameOver() {
        GameState state = createGameState(0, 200, "game-dead", 1);
        Message task = createMessage("DrigSbVG", "Steal dog delivery", 55, 5, "Walk in the park");

        service.processTurn(state, List.of(), task);

        verify(messageService, never()).solveTask(any(), any());
        verify(shopService).findPotion(any());
    }

    @Test
    void processTurnShouldSolveAdTaskWhenTaskIsValidAndGameNotOver() {
        GameState state = createGameState(3, 200, "game-valid", 1);
        Message task = createMessage("1pkQyy5i", "Steal wagon delivery", 49, 4, "Piece of cake");
        ShopItem potion = createShopItem("hpot", "Healing potion", 50);
        ShopItem upgrade = createShopItem("cs", "Claw Sharpening", 100);
        List<ShopItem> items = List.of(potion, upgrade);

        GameSolveResponse response = createGameSolveResponse(5, 300, 150, true, "Completed", 6);

        when(shopService.findPotion(any())).thenReturn(Optional.of(potion));
        when(shopService.findBestItem(any(), anyInt())).thenReturn(Optional.of(upgrade));
        when(shopService.purchaseItem("game-valid", "hpot")).thenReturn(createBuyItemResponse(3, 150, 1, 2, true));
        when(shopService.purchaseItem("game-valid", "cs")).thenReturn(createBuyItemResponse(3, 100, 1, 3, true));
        when(messageService.solveTask("game-valid", "1pkQyy5i")).thenReturn(response);

        service.processTurn(state, items, task);

        assertEquals(5, state.getLives());
        assertEquals(300, state.getGold());
        assertEquals(150, state.getScore());
        assertEquals(3, state.getActionCount());

        verify(messageService).solveTask("game-valid", "1pkQyy5i");
        verify(gameStateHelper, atLeastOnce()).updateState(eq(state), any());
    }

    @Test
    void shouldReturnTrueWhenLivesEqualDeathThreshold() {
        GameState state = createGameState(0, 100, "game-000", 0);
        assertTrue(service.isGameOver(state));
    }

    @Test
    void shouldReturnFalseWhenLivesAboveDeathThreshold() {
        GameState state = createGameState(1, 100, "game-001", 0);
        assertFalse(service.isGameOver(state));
    }

    @Test
    void shouldReturnTrueWhenActionCountEqualsExpiresIn() {
        GameState state = createGameState(3, 100, "game-002", 3);
        Message task = createMessage("YYMjMSTH", "Sheep delivery", 44, 3, "Walk in the park");
        assertTrue(service.isTaskExpired(state, task));
    }

    @Test
    void shouldReturnTrueWhenActionCountGreaterThanExpiresIn() {
        GameState state = createGameState(3, 100, "game-003", 5);
        Message task = createMessage("YYMjMSTH", "Sheep delivery", 44, 3, "Walk in the park");
        assertTrue(service.isTaskExpired(state, task));
    }

    @Test
    void shouldReturnFalseWhenActionCountLessThanExpiresIn() {
        GameState state = createGameState(3, 100, "game-004", 2);
        Message task = createMessage("NzSdD3uO", "Dog to grassland", 10, 6, "Walk in the park");
        assertFalse(service.isTaskExpired(state, task));
    }

    @Test
    void tryHealingShouldPurchasePotionAndUpdateStateWhenLivesLowAndGoldEnough() {
        GameState state = createGameState(1, 100, "game-123", 0);
        ShopItem potion = createShopItem("hpot", "Healing potion", 50);
        List<ShopItem> shopItems = List.of(potion);
        BuyItemResponse response = createBuyItemResponse(2, 44, 1, 10, true);

        when(shopService.findPotion(shopItems)).thenReturn(Optional.of(potion));
        when(shopService.purchaseItem("game-123", "hpot")).thenReturn(response);

        service.tryHealing(state, shopItems);

        assertEquals(1, state.getActionCount());
        verify(shopService).purchaseItem("game-123", "hpot");
        verify(gameStateHelper).updateState(state, response);
    }

    @Test
    void tryHealingShouldNotPurchaseWhenNoPotionFound() {
        GameState state = createGameState(1, 100, "game-456", 0);

        when(shopService.findPotion(any())).thenReturn(Optional.empty());

        service.tryHealing(state, List.of());

        assertEquals(0, state.getActionCount());
        verify(shopService, never()).purchaseItem(any(), any());
        verify(gameStateHelper, never()).updateState(any(), any());
    }

    @Test
    void tryHealingShouldNotPurchaseWhenGoldIsInsufficient() {
        GameState state = createGameState(1, 100, "game-789", 0);
        ShopItem potion = createShopItem("wingpotmax", "Potion of Awesome Wings", 300);

        when(shopService.findPotion(any())).thenReturn(Optional.of(potion));

        service.tryHealing(state, List.of(potion));

        assertEquals(0, state.getActionCount());
        verify(shopService, never()).purchaseItem(any(), any());
        verify(gameStateHelper, never()).updateState(any(), any());
    }

    @Test
    void tryHealingShouldDoNothingWhenLivesNotLow() {
        GameState state = createGameState(5, 200, "game-999", 0);
        ShopItem potion = createShopItem("hpot", "Healing potion", 50);

        service.tryHealing(state, List.of(potion));

        assertEquals(0, state.getActionCount());
        verify(shopService, never()).findPotion(any());
    }

    @Test
    void tryBuyUpgradeShouldPurchaseUpgradeAndUpdateStateWhenConditionsMet() {
        GameState state = createGameState(3, 300, "game-upgrade-1", 0);
        ShopItem upgradeItem = createShopItem("rf", "Rocket Fuel", 300);
        List<ShopItem> shopItems = List.of(upgradeItem);
        BuyItemResponse response = createBuyItemResponse(3, 100, 2, 11, true);

        when(shopService.findBestItem(shopItems, 300)).thenReturn(Optional.of(upgradeItem));
        when(shopService.purchaseItem("game-upgrade-1", "rf")).thenReturn(response);

        service.tryBuyUpgrade(state, shopItems);

        assertEquals(1, state.getActionCount());
        verify(shopService).purchaseItem("game-upgrade-1", "rf");
        verify(gameStateHelper).updateState(state, response);
    }

    @Test
    void tryBuyUpgradeShouldNotPurchaseWhenGoldIsNotEnough() {
        GameState state = createGameState(3, 90, "game-upgrade-2", 0);
        List<ShopItem> shopItems = List.of(createShopItem("cs", "Claw Sharpening", 100));

        service.tryBuyUpgrade(state, shopItems);

        assertEquals(0, state.getActionCount());
        verify(shopService, never()).purchaseItem(any(), any());
        verify(gameStateHelper, never()).updateState(any(), any());
    }

    @Test
    void tryBuyUpgradeShouldNotPurchaseWhenLivesNotAtThreshold() {
        GameState state = createGameState(2, 300, "game-upgrade-3", 0);
        List<ShopItem> shopItems = List.of(createShopItem("gas", "Gasoline", 100));

        service.tryBuyUpgrade(state, shopItems);

        assertEquals(0, state.getActionCount());
        verify(shopService, never()).findBestItem(any(), anyInt());
        verify(shopService, never()).purchaseItem(any(), any());
        verify(gameStateHelper, never()).updateState(any(), any());
    }

    @Test
    void tryBuyUpgradeShouldDoNothingWhenNoItemFound() {
        GameState state = createGameState(3, 500, "game-upgrade-4", 0);
        List<ShopItem> shopItems = List.of(createShopItem("mtrix", "Book of Megatricks", 300));

        when(shopService.findBestItem(shopItems, 500)).thenReturn(Optional.empty());

        service.tryBuyUpgrade(state, shopItems);

        assertEquals(0, state.getActionCount());
        verify(shopService, never()).purchaseItem(any(), any());
        verify(gameStateHelper, never()).updateState(any(), any());
    }

    @Test
    void solveAdTaskShouldUpdateGameStateWithSuccessResponse() {
        GameState state = createGameState(2, 100, "game-solve-success", 0);
        Message task = createMessage("DrigSbVG", "Steal dog delivery", 55, 3, "Walk in the park");

        GameSolveResponse response = createGameSolveResponse(5, 200, 150, true, "Completed", 6);

        when(messageService.solveTask("game-solve-success", "DrigSbVG")).thenReturn(response);

        service.solveAdTask(state, task);

        assertEquals(5, state.getLives());
        assertEquals(200, state.getGold());
        assertEquals(150, state.getScore());
        assertEquals(1, state.getActionCount());
        verify(messageService).solveTask("game-solve-success", "DrigSbVG");
    }

    @Test
    void solveAdTaskShouldUpdateGameStateWithFailureResponse() {
        GameState state = createGameState(3, 80, "game-solve-failure", 2);
        Message task = createMessage("bVcnjKpA", "Transport magic bucket", 6, 3, "Gamble");

        GameSolveResponse response = createGameSolveResponse(2, 60, 100, false, "Failed attempt", 7);

        when(messageService.solveTask("game-solve-failure", "bVcnjKpA")).thenReturn(response);

        service.solveAdTask(state, task);

        assertEquals(2, state.getLives());
        assertEquals(60, state.getGold());
        assertEquals(100, state.getScore());
        assertEquals(3, state.getActionCount());
        verify(messageService).solveTask("game-solve-failure", "bVcnjKpA");
    }

    private GameState createGameState(int lives, int gold, String gameId, int actionCount) {
        return GameState.builder()
                .lives(lives)
                .gold(gold)
                .gameId(gameId)
                .actionCount(actionCount)
                .build();
    }

    private ShopItem createShopItem(String id, String name, int cost) {
        return ShopItem.builder()
                .id(id)
                .name(name)
                .cost(cost)
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

    private Message createMessage(String adId, String message, int reward, int expiresIn, String probability) {
        return Message.builder()
                .adId(adId)
                .message(message)
                .reward(String.valueOf(reward))
                .expiresIn(expiresIn)
                .probability(probability)
                .build();
    }

    private GameSolveResponse createGameSolveResponse(int lives, int gold, int score, boolean success, String message, int turn) {
        return GameSolveResponse.builder()
                .lives(lives)
                .gold(gold)
                .score(score)
                .success(success)
                .message(message)
                .turn(turn)
                .build();
    }

}
