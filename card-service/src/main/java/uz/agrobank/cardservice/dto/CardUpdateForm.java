package uz.agrobank.cardservice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class CardUpdateForm {
    private String cardId;
    private String cardName;
}
