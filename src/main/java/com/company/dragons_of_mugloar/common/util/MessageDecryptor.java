package com.company.dragons_of_mugloar.common.util;

import com.company.dragons_of_mugloar.infrastructure.dragons.model.Message;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@UtilityClass
public class MessageDecryptor {

    public List<Message> decryptMessages(List<Message> messages) {
        return messages.stream()
                .map(MessageDecryptor::decrypt)
                .toList();
    }

    public Message decrypt(Message original) {
        String encryption = Optional.ofNullable(original.getEncrypted())
                .map(Object::toString)
                .map(String::toLowerCase)
                .orElse(null);

        return Message.builder()
                .adId(decryptText(original.getAdId(), encryption))
                .message(decryptText(original.getMessage(), encryption))
                .probability(decryptText(original.getProbability(), encryption))
                .reward(original.getReward())
                .expiresIn(original.getExpiresIn())
                .encrypted(original.getEncrypted())
                .build();
    }

    private String decryptText(String text, String encryption) {
        if (text == null || encryption == null) return text;
        return switch (encryption) {
            case "base64", "1" -> decodeBase64(text);
            case "rot13", "2" -> decodeRot13(text);
            default -> text;
        };
    }

    private String decodeBase64(String input) {
        try {
            return new String(Base64.getDecoder().decode(input), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return input;
        }
    }

    private String decodeRot13(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c >= 'a' && c <= 'z') sb.append((char) ((c - 'a' + 13) % 26 + 'a'));
            else if (c >= 'A' && c <= 'Z') sb.append((char) ((c - 'A' + 13) % 26 + 'A'));
            else sb.append(c);
        }
        return sb.toString();
    }
}
