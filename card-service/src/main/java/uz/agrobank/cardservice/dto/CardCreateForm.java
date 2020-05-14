package uz.agrobank.cardservice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class CardCreateForm {

    private String cardNumber;
    private String expireDate;
    private String cardName;
    private String verification;
}
