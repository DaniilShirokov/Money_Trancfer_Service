package org.netology.ru.money_transfer_service.model;

import lombok.Data;

@Data
public class ConfirmOperationObject {
    private String operationId;
    private String code;
}
