package com.company.dragons_of_mugloar.infrastructure.dragons.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Message {

    private String adId;
    private String message;
    private String reward;
    private int expiresIn;
    private Integer encrypted;
    private String probability;
    private double probabilityScore;

}
