package uz.agrobank.transactionservice.dto;

import lombok.*;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class P2PForm {
    private String cardId;
    private String recipientCardId;
    private BigInteger amount;
}
