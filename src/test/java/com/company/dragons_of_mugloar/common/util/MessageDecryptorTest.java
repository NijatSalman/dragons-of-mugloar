package com.company.dragons_of_mugloar.common.util;

import com.company.dragons_of_mugloar.infrastructure.dragons.model.Message;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageDecryptorTest {

    @Test
    void decryptShouldDecodeBase64EncryptedFields() {
        Message encryptedMessage = createMessage(
                base64("DrigSbVG"),
                base64("Steal dog delivery"),
                base64("Walk in the park"),
                3,
                1
        );

        Message result = MessageDecryptor.decrypt(encryptedMessage);

        assertEquals("DrigSbVG", result.getAdId());
        assertEquals("Steal dog delivery", result.getMessage());
        assertEquals("Walk in the park", result.getProbability());
        assertEquals(3, result.getExpiresIn());
        assertEquals(1, result.getEncrypted());
    }

    @Test
    void decryptShouldDecodeRot13EncryptedFields() {
        Message encryptedMessage = createMessage(
                rot13("NzSdD3uO"),
                rot13("Help transport dog"),
                rot13("Sure thing"),
                4,
                2
        );

        Message result = MessageDecryptor.decrypt(encryptedMessage);

        assertEquals("NzSdD3uO", result.getAdId());
        assertEquals("Help transport dog", result.getMessage());
        assertEquals("Sure thing", result.getProbability());
        assertEquals(4, result.getExpiresIn());
        assertEquals(2, result.getEncrypted());
    }

    @Test
    void decryptShouldReturnOriginalWhenEncryptionIsNull() {
        Message plainMessage = createMessage("ad123", "plain text", "Gamble", 3, null);

        Message result = MessageDecryptor.decrypt(plainMessage);

        assertEquals("ad123", result.getAdId());
        assertEquals("plain text", result.getMessage());
        assertEquals("Gamble", result.getProbability());
    }

    @Test
    void decryptMessagesShouldDecryptAllInList() {
        Message m1 = createMessage(base64("abc"), base64("msg1"), base64("Sure thing"), 3, 1);
        Message m2 = createMessage(rot13("def"), rot13("msg2"), rot13("Walk in the park"), 4, 2);

        List<Message> result = MessageDecryptor.decryptMessages(List.of(m1, m2));

        assertEquals(2, result.size());
        assertEquals("abc", result.get(0).getAdId());
        assertEquals("def", result.get(1).getAdId());
        assertEquals("msg1", result.get(0).getMessage());
        assertEquals("msg2", result.get(1).getMessage());
    }

    @Test
    void decryptShouldFallbackToOriginalWhenBase64IsInvalid() {
        Message invalid = createMessage("!@#INVALID", "!@#INVALID", "!@#INVALID", 3, 1);

        Message result = MessageDecryptor.decrypt(invalid);

        assertEquals("!@#INVALID", result.getAdId());
        assertEquals("!@#INVALID", result.getMessage());
        assertEquals("!@#INVALID", result.getProbability());
    }

    private Message createMessage(String adId, String message, String probability, int expiresIn, Integer encrypted) {
        return Message.builder()
                .adId(adId)
                .message(message)
                .probability(probability)
                .reward("10")
                .expiresIn(expiresIn)
                .encrypted(encrypted)
                .build();
    }

    private String base64(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String rot13(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c >= 'a' && c <= 'z') sb.append((char) ((c - 'a' + 13) % 26 + 'a'));
            else if (c >= 'A' && c <= 'Z') sb.append((char) ((c - 'A' + 13) % 26 + 'A'));
            else sb.append(c);
        }
        return sb.toString();
    }
}