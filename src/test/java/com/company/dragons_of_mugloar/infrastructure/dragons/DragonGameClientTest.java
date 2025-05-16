package com.company.dragons_of_mugloar.infrastructure.dragons;

import com.company.dragons_of_mugloar.common.config.DragonApiProperties;
import com.company.dragons_of_mugloar.common.exception.ClientException;
import com.company.dragons_of_mugloar.common.exception.GameClientException;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DragonGameClientTest {

    private RestTemplate restTemplate;
    private DragonApiProperties properties;
    private DragonGameClient client;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        properties = mock(DragonApiProperties.class);
        client = new DragonGameClient(restTemplate, properties);
        when(properties.getUrl()).thenReturn("http://localhost/api");
    }

    @Test
    void startGameShouldReturnResponse() {
        GameStartResponse response = GameStartResponse.builder()
                .gameId("game-001")
                .lives(3)
                .gold(100)
                .score(0)
                .level(1)
                .highScore(0)
                .turn(1)
                .build();

        ResponseEntity<GameStartResponse> entity = new ResponseEntity<>(response, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(GameStartResponse.class)))
                .thenReturn(entity);

        GameStartResponse result = client.startGame();

        assertAll(
                () -> assertEquals("game-001", result.getGameId()),
                () -> assertEquals(3, result.getLives()),
                () -> assertEquals(100, result.getGold()),
                () -> assertEquals(1, result.getLevel()),
                () -> assertEquals(0, result.getScore()),
                () -> assertEquals(0, result.getHighScore()),
                () -> assertEquals(1, result.getTurn())
        );
    }

    @Test
    void getMessagesShouldReturnDecryptedMessages() {
        List<Message> encryptedMessages = List.of(
                Message.builder().adId("abc").message("task").probability("Sure thing").expiresIn(3).build()
        );

        ResponseEntity<List<Message>> entity = new ResponseEntity<>(encryptedMessages, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(entity);

        List<Message> result = client.getMessages("game-002");

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("abc", result.get(0).getAdId()),
                () -> assertEquals("task", result.get(0).getMessage()),
                () -> assertEquals("Sure thing", result.get(0).getProbability()),
                () -> assertEquals(3, result.get(0).getExpiresIn())
        );
    }

    @Test
    void getShopItemsShouldReturnItems() {
        List<ShopItem> items = List.of(
                ShopItem.builder().id("tricks").name("Book of Tricks").cost(100).build()
        );

        ResponseEntity<List<ShopItem>> entity = new ResponseEntity<>(items, HttpStatus.OK);
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(entity);

        List<ShopItem> result = client.getShopItems("game-003");

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("tricks", result.get(0).getId()),
                () -> assertEquals("Book of Tricks", result.get(0).getName()),
                () -> assertEquals(100, result.get(0).getCost())
        );
    }

    @Test
    void solveAdShouldReturnResponse() {
        GameSolveResponse response = GameSolveResponse.builder()
                .lives(3)
                .gold(150)
                .score(100)
                .highScore(120)
                .success(true)
                .turn(5)
                .message("Completed")
                .build();

        ResponseEntity<GameSolveResponse> entity = new ResponseEntity<>(response, HttpStatus.OK);
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.POST), any(), eq(GameSolveResponse.class)))
                .thenReturn(entity);

        GameSolveResponse result = client.solveAd("game-004", "ad123");

        assertAll(
                () -> assertEquals(3, result.getLives()),
                () -> assertEquals(150, result.getGold()),
                () -> assertEquals(100, result.getScore()),
                () -> assertEquals(120, result.getHighScore()),
                () -> assertTrue(result.getSuccess()),
                () -> assertEquals(5, result.getTurn()),
                () -> assertEquals("Completed", result.getMessage())
        );
    }

    @Test
    void purchaseItemShouldReturnResponse() {
        BuyItemResponse response = BuyItemResponse.builder()
                .gold(50)
                .lives(3)
                .level(1)
                .turn(4)
                .shoppingSuccess(true)
                .build();

        ResponseEntity<BuyItemResponse> entity = new ResponseEntity<>(response, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(BuyItemResponse.class)))
                .thenReturn(entity);

        BuyItemResponse result = client.purchaseItem("game-005", "hpot");

        assertAll(
                () -> assertEquals(3, result.getLives()),
                () -> assertEquals(50, result.getGold()),
                () -> assertEquals(1, result.getLevel()),
                () -> assertEquals(4, result.getTurn()),
                () -> assertTrue(result.isShoppingSuccess())
        );
    }

    @Test
    void startGameShouldThrowGameClientExceptionOnError() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(GameStartResponse.class)))
                .thenThrow(new RuntimeException("fail"));

        assertThrows(GameClientException.class, () -> client.startGame());
    }

    @Test
    void getMessagesShouldThrowClientExceptionOnError() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("fail"));

        assertThrows(ClientException.class, () -> client.getMessages("game-err"));
    }

    @Test
    void getShopItemsShouldThrowClientExceptionOnError() {
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("fail"));

        assertThrows(ClientException.class, () -> client.getShopItems("game-err"));
    }

    @Test
    void solveAdShouldThrowClientExceptionOnError() {
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.POST), any(), eq(GameSolveResponse.class)))
                .thenThrow(new RuntimeException("fail"));

        assertThrows(ClientException.class, () -> client.solveAd("game-err", "ad-err"));
    }

    @Test
    void purchaseItemShouldThrowClientExceptionOnError() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(BuyItemResponse.class)))
                .thenThrow(new RuntimeException("fail"));

        assertThrows(ClientException.class, () -> client.purchaseItem("game-err", "item-err"));
    }
}