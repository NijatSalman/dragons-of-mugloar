package com.company.dragons_of_mugloar.infrastructure.dragons.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShopItem {

    private String id;
    private String name;
    private int cost;

}
