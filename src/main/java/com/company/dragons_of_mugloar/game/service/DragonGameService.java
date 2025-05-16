package com.company.dragons_of_mugloar.game.service;

import com.company.dragons_of_mugloar.game.model.DragonGameResponse;
import com.company.dragons_of_mugloar.game.model.GameState;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.GameStartResponse;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.ShopItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DragonGameService {

    private final GameService gameService;
    private final ShopService shopService;
    private final GamePlayerService gamePlayerService;

    public DragonGameResponse startGame() {
        GameStartResponse start = gameService.startGame();

        GameState state = GameState.builder()
                .gameId(start.getGameId())
                .lives(start.getLives())
                .gold(start.getGold())
                .score(start.getScore())
                .actionCount(0)
                .build();
        log.info("Game started with ID: {}, Lives: {}, Gold: {}, Score: {}",
                state.getGameId(), state.getLives(), state.getGold(), state.getScore());

        List<ShopItem> shopItems = shopService.getShopItems(state.getGameId());
        return gamePlayerService.play(shopItems, state);
    }

}
