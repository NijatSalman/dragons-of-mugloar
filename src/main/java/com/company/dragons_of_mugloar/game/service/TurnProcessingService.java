package com.company.dragons_of_mugloar.game.service;

import com.company.dragons_of_mugloar.game.model.GameState;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.Message;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.ShopItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.company.dragons_of_mugloar.game.model.GameConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TurnProcessingService {

    private final MessageService messageService;
    private final ShopService shopService;
    private final GameStateHelper gameStateHelper;

    public void processTurn(GameState state, List<ShopItem> shopItems, Message task) {
        log.debug("Processing turn for task: {}, game state: {}", task.getAdId(), state);
        tryHealing(state, shopItems);
        tryBuyUpgrade(state, shopItems);

        if (isTaskExpired(state, task)) {
            log.debug("Skipping expired task {} (actionCount={}, expiresIn={})",
                    task.getAdId(), state.getActionCount(), task.getExpiresIn());
            return;
        }

        if (isGameOver(state)) {
            log.debug("Lives are {} and you lost game!", DEATH_THRESHOLD);
            return;
        }

        solveAdTask(state, task);
    }

    protected boolean isGameOver(GameState state) {
        return state.getLives() == DEATH_THRESHOLD;
    }

    protected boolean isTaskExpired(GameState state, Message task) {
        return state.getActionCount() >= task.getExpiresIn();
    }


    protected void tryHealing(GameState state, List<ShopItem> items) {
        if (state.getLives() < BASE_LIVES_THRESHOLD) {
            log.info("Lives are less than {}, trying to find healing potion...", BASE_LIVES_THRESHOLD);
            shopService.findPotion(items).ifPresent(p -> {
                if (state.getGold() >= p.getCost()) {
                    var result = shopService.purchaseItem(state.getGameId(), p.getId());
                    state.incrementActions();
                    gameStateHelper.updateState(state, result);
                }
            });
        }
    }

    protected void tryBuyUpgrade(GameState state, List<ShopItem> items) {
        if (state.getGold() > GOLD_THRESHOLD && state.getLives() == BASE_LIVES_THRESHOLD) {
            log.info("Buying upgrade item as gold is sufficient and lives are at maximum...");
            shopService.findBestItem(items, state.getGold()).ifPresent(i -> {
                var result = shopService.purchaseItem(state.getGameId(), i.getId());
                state.incrementActions();
                gameStateHelper.updateState(state, result);
            });
        }
    }

    protected void solveAdTask(GameState state, Message task) {
        log.debug("Solving ad task with ad ID: {}", task.getAdId());
        var result = messageService.solveTask(state.getGameId(), task.getAdId());
        state.setLives(result.getLives());
        state.setGold(result.getGold());
        state.setScore(result.getScore());
        state.incrementActions();
        log.debug("Ad task solved with ad ID: {}, updated state: lives = {}, gold = {}, score = {}, probability = {}, success: {}, action count = {}",
                task.getAdId(),
                state.getLives(),
                state.getGold(),
                state.getScore(),
                task.getProbability(),
                result.getSuccess(),
                state.getActionCount());
    }
}
