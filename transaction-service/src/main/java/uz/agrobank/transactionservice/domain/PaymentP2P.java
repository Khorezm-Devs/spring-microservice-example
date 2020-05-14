package uz.agrobank.transactionservice.domain;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class PaymentP2P {
    private String recipientCardId;
}
