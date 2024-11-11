package org.netology.ru.money_transfer_service.model;

import lombok.Data;

@Data
public class ErrorResponse {
    private String message;
    private int id;
}