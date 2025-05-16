package com.company.dragons_of_mugloar.common.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomErrorResponse {
    private int status;
    private String message;
}
