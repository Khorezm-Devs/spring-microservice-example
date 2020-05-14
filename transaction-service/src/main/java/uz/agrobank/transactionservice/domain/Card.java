package uz.agrobank.transactionservice.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Document(collection = "card")
public class Card {
    @Id
    private String id;
    /**
     * Удалённый ИД карты
     */
    private String remoteId;
    private String localCardName;

    // UzCard
    private String userId;
    private String pan;
    private String expiry;
    private String phone;
    private String fullName;
    private BigInteger balance;
    private Boolean sms;
    private Integer pincnt;
    private String aacct;
    private String cardType;
    private String holdAmount;
    private String cashBackAmount;
}
