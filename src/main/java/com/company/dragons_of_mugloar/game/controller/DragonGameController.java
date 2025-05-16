package com.company.dragons_of_mugloar.game.controller;

import com.company.dragons_of_mugloar.game.model.DragonGameResponse;
import com.company.dragons_of_mugloar.game.service.DragonGameService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "dragon", description = "APIs for dragon train process game")
@RequestMapping(value = "/internal-api/v2/game")
@RequiredArgsConstructor
public class DragonGameController {

    private final DragonGameService gameService;

    @PostMapping("/start")
    public DragonGameResponse startGame() {
        log.debug("Dragon game started ...");
        return gameService.startGame();
    }
}
