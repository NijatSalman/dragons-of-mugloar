package com.company.dragons_of_mugloar.infrastructure.dragons;

import com.company.dragons_of_mugloar.common.config.DragonApiProperties;
import com.company.dragons_of_mugloar.common.exception.GameClientException;
import com.company.dragons_of_mugloar.common.exception.ClientException;
import com.company.dragons_of_mugloar.common.util.MessageDecryptor;
import com.company.dragons_of_mugloar.infrastructure.dragons.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DragonGameClient {

    private final RestTemplate restTemplate;
    private final DragonApiProperties properties;

    private <T> HttpEntity<T> createJsonHttpEntity(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    public GameStartResponse startGame() {
        try {
            String url = UriComponentsBuilder.fromUriString(properties.getUrl())
                    .path("/game/start")
                    .toUriString();

            HttpEntity<Void> entity = createJsonHttpEntity(null);

            ResponseEntity<GameStartResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    GameStartResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Error while starting game. cause: {}", e.getLocalizedMessage());
            throw new GameClientException("Error while starting game.",e);
        }

    }

    public List<Message> getMessages(String gameId) {
        try {
            String url = UriComponentsBuilder.fromUriString(properties.getUrl())
                    .pathSegment(gameId, "messages")
                    .toUriString();

            HttpEntity<Void> entity = createJsonHttpEntity(null);

            ResponseEntity<List<Message>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            return MessageDecryptor.decryptMessages(response.getBody());
        } catch (Exception e) {
            String message = String.format("Error while getting messages for gameId: %s", gameId);
            log.error("{} cause: {}", message, e.getLocalizedMessage());
            throw new ClientException(message,e);
        }

    }

    public List<ShopItem> getShopItems(String gameId) {
        try {
            URI uri = UriComponentsBuilder
                    .fromUri(URI.create(properties.getUrl()))
                    .pathSegment(gameId, "shop")
                    .build()
                    .toUri();

            HttpEntity<Void> entity = createJsonHttpEntity(null);

            ResponseEntity<List<ShopItem>> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            return response.getBody();
        } catch (Exception e) {
            String message = String.format("Error while getting shop items for gameId: %s", gameId);
            log.error("{} cause: {}", message, e.getLocalizedMessage());
            throw new ClientException(message,e);
        }

    }

    public GameSolveResponse solveAd(String gameId, String adId) {
        try {
            URI uri = UriComponentsBuilder
                    .fromHttpUrl(properties.getUrl())
                    .pathSegment(gameId, "solve", adId)
                    .build()
                    .toUri();

            HttpEntity<Void> entity = createJsonHttpEntity(null);

            ResponseEntity<GameSolveResponse> response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    entity,
                    GameSolveResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            String message = String.format("Error while solving ad task for gameId: %s and taskId: %s", gameId, adId);
            log.error("{} cause: {}", message, e.getLocalizedMessage());
            throw new ClientException(message,e);
        }

    }

    public BuyItemResponse purchaseItem(String gameId, String itemId) {
        try {
            String url = UriComponentsBuilder.fromUriString(properties.getUrl())
                    .pathSegment(gameId, "shop", "buy", itemId)
                    .toUriString();

            HttpEntity<Void> entity = createJsonHttpEntity(null);

            ResponseEntity<BuyItemResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    BuyItemResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            String message = String.format("Error while purchase item for gameId: %s and itemId: %s", gameId, itemId);
            log.error("{} cause: {}", message, e.getLocalizedMessage());
            throw new ClientException(message,e);
        }

    }
}