package uz.agrobank.transactionservice.domain;


import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Document(collection = "payment")
public class Payment implements Serializable {

    @Id
    private String id;
    private String userId;
    private String cardId;
    private PaymentType paymentType;
    private String merchantId;
    private String terminalId;
    private Integer port;
    private Integer stan;
    private BigInteger amount;
    private String date7;
    private String date12;
    private Object data;
}
