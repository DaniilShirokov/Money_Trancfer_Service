package org.netology.ru.money_transfer_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class InputObjectForTrancfer {
    @JsonProperty("cardFromNumber")
    private String cardFromNumber;

    @JsonProperty("cardFromValidTill")
    private String cardFromValidTill;

    @JsonProperty("cardFromCVV")
    private String cardFromCVV;

    @JsonProperty("cardToNumber")
    private String cardToNumber;

    @JsonProperty("amount")
    private Amount amount;


    public InputObjectForTrancfer(String cardFromNumber, String cardFromValidTill, String cardFromCVV, String cardToNumber, Amount amount) {
        this.cardFromNumber = cardFromNumber;
        this.cardFromValidTill = cardFromValidTill;
        this.cardFromCVV = cardFromCVV;
        this.cardToNumber = cardToNumber;
        this.amount = amount;
    }
}
