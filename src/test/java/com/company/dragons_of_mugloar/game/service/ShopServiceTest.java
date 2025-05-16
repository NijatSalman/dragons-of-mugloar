package com.company.dragons_of_mugloar.game.service;

import com.company.dragons_of_mugloar.infrastructure.dragons.DragonGameClient;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.BuyItemResponse;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.ShopItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShopServiceTest {

    private DragonGameClient gameClient;
    private ShopService shopService;

    @BeforeEach
    void setUp() {
        gameClient = mock(DragonGameClient.class);
        shopService = new ShopService(gameClient);
    }

    @Test
    void getShopItemsShouldReturnItemsFromClient() {
        String gameId = "game-777";
        List<ShopItem> items = List.of(
                createShopItem("rf", "Rocket Fuel", 300),
                createShopItem("hpot", "Healing Potion", 50)
        );

        when(gameClient.getShopItems(gameId)).thenReturn(items);

        List<ShopItem> result = shopService.getShopItems(gameId);

        assertEquals(items, result);
        verify(gameClient).getShopItems(gameId);
    }

    @Test
    void purchaseItemShouldReturnBuyItemResponseFromClient() {
        String gameId = "game-123";
        String itemId = "tricks";

        BuyItemResponse response = createBuyItemResponse(3, 100, 2, 5, true);

        when(gameClient.purchaseItem(gameId, itemId)).thenReturn(response);

        BuyItemResponse result = shopService.purchaseItem(gameId, itemId);

        assertEquals(response, result);
        verify(gameClient).purchaseItem(gameId, itemId);
    }

    @Test
    void findPotionShouldReturnPotionIfPresent() {
        List<ShopItem> items = List.of(
                createShopItem("wing", "Wings Upgrade", 100),
                createShopItem("hpot", "Healing Potion", 50)
        );

        Optional<ShopItem> result = shopService.findPotion(items);

        assertTrue(result.isPresent());
        assertEquals("hpot", result.get().getId());
    }

    @Test
    void findPotionShouldReturnEmptyIfNoPotionPresent() {
        List<ShopItem> items = List.of(
                createShopItem("rf", "Rocket Fuel", 300),
                createShopItem("cs", "Claw Sharpening", 100)
        );

        Optional<ShopItem> result = shopService.findPotion(items);

        assertTrue(result.isEmpty());
    }

    @Test
    void findBestItemShouldReturnOneItemWithinGoldRange() {
        List<ShopItem> items = List.of(
                createShopItem("cheap", "Basic Armor", 50),
                createShopItem("medium", "Claw Sharpening", 150),
                createShopItem("expensive", "Iron Plating", 400)
        );

        int gold = 200;

        Optional<ShopItem> result = shopService.findBestItem(items, gold);

        assertTrue(result.isPresent());
        assertTrue(result.get().getCost() <= gold - 50);
    }

    @Test
    void findBestItemShouldReturnEmptyIfNoAffordableItems() {
        List<ShopItem> items = List.of(
                createShopItem("expensive", "Dragon Hide", 1000)
        );

        Optional<ShopItem> result = shopService.findBestItem(items, 200);

        assertTrue(result.isEmpty());
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
}