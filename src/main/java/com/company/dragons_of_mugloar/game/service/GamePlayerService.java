package com.company.dragons_of_mugloar.game.service;


import com.company.dragons_of_mugloar.game.model.AdTaskProbability;
import com.company.dragons_of_mugloar.game.model.DragonGameResponse;
import com.company.dragons_of_mugloar.game.model.GameState;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.Message;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.ShopItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.company.dragons_of_mugloar.game.model.GameConstants.MAX_ACTIONS;
import static com.company.dragons_of_mugloar.game.model.GameConstants.MIN_ACTIONS;

@Slf4j
@Service
@RequiredArgsConstructor
public class GamePlayerService {

    private final GameStateHelper gameStateHelper;
    private final MessageService messageService;
    private final TurnProcessingService turnProcessingService;

    public DragonGameResponse play(List<ShopItem> shopItems, GameState state) {
        if (gameStateHelper.isGameOver(state)) {
            log.info("Game over or target score reached. Finalizing the game. Game state: {}, ", state);
            return gameStateHelper.buildFinalResponse(state);
        }

        List<Message> tasks = messageService.getPrioritizedTasks(state.getGameId());
        int maxActions = determineMaxActions(tasks);

        log.debug("Prioritized tasks: {}, for game id: {} with max available action: {}", tasks, state.getGameId(), maxActions);

        for (Message task : tasks) {

            turnProcessingService.processTurn(state, shopItems, task);

            if (state.getActionCount() >= maxActions) {
                log.info("Action count limit reached. Restarting turn processing...");
                state.resetActionCount();
                return play(shopItems, state);
            }
        }

        state.resetActionCount();
        return play(shopItems, state);
    }

    private int determineMaxActions(List<Message> tasks) {
        int totalWeight = tasks.stream()
                .mapToInt(m -> AdTaskProbability.getWeight(m.getProbability()))
                .sum();
        int maxActions = Math.max(MIN_ACTIONS, totalWeight / 10);
        return Math.min(maxActions, MAX_ACTIONS);
    }
}
