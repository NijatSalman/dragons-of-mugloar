package com.company.dragons_of_mugloar.game.service;

import com.company.dragons_of_mugloar.infrastructure.dragons.DragonGameClient;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.BuyItemResponse;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.ShopItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {

    private final DragonGameClient gameClient;

    public List<ShopItem> getShopItems(String gameId) {
        log.debug("Fetching shop items for game ID: {}", gameId);
        return gameClient.getShopItems(gameId);
    }

    public BuyItemResponse purchaseItem(String gameId, String itemId) {
        log.info("Purchasing item {} for game ID: {}", itemId, gameId);
        return gameClient.purchaseItem(gameId, itemId);
    }

    public Optional<ShopItem> findPotion(List<ShopItem> items) {
        return items.stream()
                .filter(i -> i.getName().toLowerCase().contains("potion"))
                .findFirst();
    }

    public Optional<ShopItem> findBestItem(List<ShopItem> items, int gold) {
        List<ShopItem> candidates = items.stream()
                .filter(i -> i.getCost() <= gold - 50)
                .toList();
        if (candidates.isEmpty()) return Optional.empty();
        return Optional.of(candidates.get(new Random().nextInt(candidates.size())));
    }
}
