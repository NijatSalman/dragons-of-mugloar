package com.company.dragons_of_mugloar.game.service;

import com.company.dragons_of_mugloar.infrastructure.dragons.DragonGameClient;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.GameStartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

    private final DragonGameClient client;

    public GameStartResponse startGame() {
        return client.startGame();
    }
}
