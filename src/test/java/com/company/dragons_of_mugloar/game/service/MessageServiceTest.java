package com.company.dragons_of_mugloar.game.service;

import com.company.dragons_of_mugloar.infrastructure.dragons.DragonGameClient;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.GameSolveResponse;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    private DragonGameClient client;
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        client = mock(DragonGameClient.class);
        messageService = new MessageService(client);
    }

    @Test
    void solveTaskShouldReturnResponseFromClient() {
        String gameId = "game-321";
        String adId = "YYMjMSTH";

        GameSolveResponse expectedResponse = GameSolveResponse.builder()
                .lives(3)
                .gold(120)
                .score(50)
                .success(true)
                .turn(4)
                .highScore(70)
                .message("Task solved")
                .build();

        when(client.solveAd(gameId, adId)).thenReturn(expectedResponse);

        GameSolveResponse actual = messageService.solveTask(gameId, adId);

        assertEquals(expectedResponse, actual);
        verify(client).solveAd(gameId, adId);
    }

    @Test
    void getPrioritizedTasksShouldFilterUnknownProbabilityAndSortByScore() {
        String gameId = "game-999";
        List<Message> mockTasks = List.of(
                createMessage("1", "msg1", "40", 3, "Piece of cake"),
                createMessage("2", "msg2", "20", 5, "Hmmm...."),
                createMessage("3", "msg3", "15", 4, "Unknown"),
                createMessage("4", "msg4", "30", 1, "Sure thing")
        );

        when(client.getMessages(gameId)).thenReturn(mockTasks);

        List<Message> result = messageService.getPrioritizedTasks(gameId);

        assertEquals(3, result.size());
        assertTrue(result.stream().noneMatch(m -> "Unknown".equalsIgnoreCase(m.getProbability())));

        assertTrue(result.stream().allMatch(m -> m.getProbabilityScore() > 0));

        List<Double> scores = result.stream()
                .map(Message::getProbabilityScore)
                .toList();

        assertTrue(scores.get(0) >= scores.get(1));
        assertTrue(scores.get(1) >= scores.get(2));

        verify(client).getMessages(gameId);
    }

    private Message createMessage(String adId, String msg, String reward, int expiresIn, String probability) {
        return Message.builder()
                .adId(adId)
                .message(msg)
                .reward(reward)
                .expiresIn(expiresIn)
                .probability(probability)
                .build();
    }
}
