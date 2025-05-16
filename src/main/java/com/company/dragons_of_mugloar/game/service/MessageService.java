package com.company.dragons_of_mugloar.game.service;

import com.company.dragons_of_mugloar.game.model.AdTaskProbability;
import com.company.dragons_of_mugloar.infrastructure.dragons.DragonGameClient;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.GameSolveResponse;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final DragonGameClient client;

    public GameSolveResponse solveTask(String gameId, String adId) {
        log.info("Solving ad {} for game ID: {}", adId, gameId);
        return client.solveAd(gameId, adId);
    }

    public List<Message> getPrioritizedTasks(String gameId) {
        log.debug("Fetching and prioritizing tasks for gameId: {}", gameId);

        return client.getMessages(gameId).stream()
                .filter(this::isNotUnknownProbability)
                .peek(message -> message.setProbabilityScore(calculateAdScore(message)))
                .sorted(Comparator.comparingDouble(Message::getProbabilityScore).reversed())
                .collect(Collectors.toList());
    }

    private boolean isNotUnknownProbability(Message message) {
        return !"Unknown".equalsIgnoreCase(message.getProbability());
    }

    private double calculateAdScore(Message message) {
        int probabilityWeight = AdTaskProbability.getWeight(message.getProbability());
        int expiresIn = message.getExpiresIn();
        double score = probabilityWeight + (10.0 / (expiresIn + 1));
        log.debug("taskId: {} has priority score: {}", message.getAdId(), score);
        return score;
    }

}
